package com.alistats.discorki.tasks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alistats.discorki.dto.discord.EmbedDto;
import com.alistats.discorki.dto.discord.WebhookDto;
import com.alistats.discorki.dto.riot.spectator.CurrentGameInfoDto;
import com.alistats.discorki.dto.riot.spectator.ParticipantDto;
import com.alistats.discorki.model.Summoner;
import com.alistats.discorki.notification.common.IGameStartNotification;

@Component
/**
 * This class is used to check if the user is in game.
 */
public final class CheckJustInGameTask extends Task {
    @Autowired
    private List<IGameStartNotification> gameStartNotificationCheckers;

    // Run every 5 minutes.
    @Scheduled(cron = "0/30 * * 1/1 * ?")
    public void checkJustInGame() {
        logger.info("Checking if users are in game.");

        // A list of summoners that a game was found for and wont be checked again
        ArrayList<Summoner> skiplist = new ArrayList<Summoner>();

        // Get all tracked summoners from the database
        ArrayList<Summoner> summoners = summonerRepo.findByIsTracked(true).orElseThrow();
        summoners
                .stream()
                .filter(s -> !s.isInGame())
                .filter(s -> !skiplist.contains(s))
                .filter(s -> getCurrentGame(s.getId()) != null)
                .forEach(s -> {
                    // Get participants from current game and check if other
                    // tracked summoners are in the game. If so, add to skiplist
                    CurrentGameInfoDto game = getCurrentGame(s.getId());
                    ArrayList<Summoner> trackedSummonersInGame = filterTrackedSummoners(summoners, game.getParticipants());

                    // Set the current game id for each summoner
                    trackedSummonersInGame.forEach(tsig -> {
                        logger.info("{} is now in game.", tsig.getName());
                        tsig.setCurrentGameId(game.getGameId());
                        summonerRepo.save(tsig);
                    });

                    // Check for notable events
                    checkForNotableEvents(game);

                    skiplist.addAll(trackedSummonersInGame);
                });

    }

    private CurrentGameInfoDto getCurrentGame(String summonerId) {
        // TODO: use spring retry for this
        int retryCount = 0;
        int maxTries = 2;
        while (retryCount < maxTries) {
            try {
                return leagueApiController.getCurrentGameInfo(summonerId);
            } catch (Exception e) {
                if (e.getMessage().contains("404")) {
                    return null;
                } else if (e.getMessage().contains("429")) {
                    logger.warn("Rate limit exceeded! Waiting 10 seconds...");
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e1) {
                        logger.warn("Thread sleep interrupted!");
                    }
                }
            }
        }
        return null;
    }

    private void checkForNotableEvents(CurrentGameInfoDto currentGameInfo) {
        try {
            // Get embeds from all PostGameNotifications
            ArrayList<EmbedDto> embeds = new ArrayList<EmbedDto>();

            ExecutorService executor = Executors.newFixedThreadPool(4);

            gameStartNotificationCheckers
                    .forEach(checker -> {
                        executor.execute(() -> {
                            logger.info("Checking for '{}' for {}", checker.getClass().getName(), currentGameInfo.getGameId());
                            embeds.addAll(checker.check(currentGameInfo));
                        });
                    });

            executor.awaitTermination(5L, TimeUnit.SECONDS);

            if (embeds.size() == 0) {
                logger.info("No notable events found for game {}", currentGameInfo.getGameId());
                return;
            }

            WebhookDto webhookDto = webhookBuilder.build(embeds);
            discordController.sendWebhook(webhookDto);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }

    public ArrayList<Summoner> filterTrackedSummoners(ArrayList<Summoner> trackedSummoners,
            ParticipantDto[] participants) {
        return trackedSummoners
                .stream()
                .filter(s -> Arrays.stream(participants)
                        .anyMatch(p -> p.getSummonerName().equals(s.getName())))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}