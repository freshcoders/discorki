package com.alistats.discorki.tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alistats.discorki.dto.discord.EmbedDto;
import com.alistats.discorki.dto.discord.WebhookDto;
import com.alistats.discorki.dto.riot.spectator.CurrentGameInfoDto;
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
    @Scheduled(cron = "0 0/5 * 1/1 * ?")
    public void checkJustInGame() {
        logger.info("Checking if users are in game.");

        // Get all registered summoners from the database
        summonerRepo.findByIsTracked(true).orElseThrow().parallelStream()
                .filter(s -> !s.isInGame())
                .filter(s -> {
                    // TODO: move this to another class (maybe just the controller)
                    int retryCount = 0;
                    int maxTries = 2;
                    while (retryCount < maxTries) {
                        try {
                            return leagueApiController.getCurrentGameInfo(s.getId()) != null;
                        } catch (Exception e) {
                            if (e.getMessage().contains("404")) {
                                return false;
                            } else if (e.getMessage().contains("429")) {
                                logger.warn("Rate limit exceeded! Waiting 15 seconds...");
                                try {
                                    Thread.sleep(15000);
                                } catch (InterruptedException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                    }
                    return false;
                })
                .forEach(s -> {
                    logger.info("User " + s.getName() + " is now in game.");
                    try {
                        CurrentGameInfoDto currentGameInfoDto = leagueApiController.getCurrentGameInfo(s.getId());
                        if (currentGameInfoDto != null) {
                            s.setCurrentGameId(currentGameInfoDto.getGameId());
                            summonerRepo.save(s);
                            logger.info("User " + s.getName() + " is now in game.");
                            checkForNotableEvents(s, currentGameInfoDto);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error(e.getMessage());
                    }
                });
    }

    private void checkForNotableEvents(Summoner summoner, CurrentGameInfoDto currentGameInfo) {
        try {
            // Get embeds from all PostGameNotifications
            ArrayList<EmbedDto> embeds = new ArrayList<EmbedDto>();

            ExecutorService executor = Executors.newFixedThreadPool(4);

            gameStartNotificationCheckers
                    .forEach(checker -> {
                        executor.execute(() -> {
                            embeds.addAll(checker.check(currentGameInfo));
                        });
                    });
            
             executor.awaitTermination(5L, TimeUnit.SECONDS);

            if (embeds.size() == 0) {
                logger.info("No notable events found for " + summoner.getName());
                return;
            }
            
            WebhookDto webhookDto = webhookBuilder.build(embeds);
            discordController.sendWebhook(webhookDto);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }
}