package com.alistats.discorki.tasks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alistats.discorki.model.Match;
import com.alistats.discorki.model.Match.Status;
import com.alistats.discorki.model.Summoner;
import com.alistats.discorki.notification.game_start.GameStartNotification;
import com.alistats.discorki.notification.result.GameStartNotificationResult;
import com.alistats.discorki.riot.dto.spectator.CurrentGameInfoDto;
import com.alistats.discorki.riot.dto.spectator.ParticipantDto;

import net.dv8tion.jda.api.entities.MessageEmbed;

@Component
/**
 * This class is used to check if the user is in game.
 */
public final class CheckJustInGameTask extends Task {
    @Autowired
    private List<GameStartNotification> gameStartNotificationCheckers;

    // Run every 5 minutes.
    @Scheduled(cron = "0 0/5 * 1/1 * ?")
    public void checkJustInGame() {
        logger.info("Running task {}", this.getClass().getSimpleName());

        // A list of summoners that a game was found for and wont be checked again
        ArrayList<Summoner> skiplist = new ArrayList<Summoner>();

        // Get all tracked summoners from the database
        // for each active guild, get all summoners and put them in A SET
        Set<Summoner> summonersToCheck = guildRepo.findByActiveTrue().stream()
                .flatMap(g -> g.getSummoners().stream()).collect(Collectors.toSet());

        logger.debug("Got {} summoners to check from db", summonersToCheck.size());

        // Get all matches in progress from the database
        ArrayList<Match> matchesInProgress = matchRepo.findByStatus(Status.IN_PROGRESS).orElseThrow();
        logger.debug("Got {} matches in progress from db", matchesInProgress.size());

        // Define temp game for storing the current game to reduce api calls
        AtomicReference<CurrentGameInfoDto> tempGame = new AtomicReference<CurrentGameInfoDto>();
        
        summonersToCheck
                .stream()
                .filter(s -> !skiplist.contains(s))
                .filter(s -> {
                    CurrentGameInfoDto currentGameInfoDto = getCurrentGame(s.getId());
                    if (currentGameInfoDto != null) {
                        if (s.getCurrentMatch() != null) {
                            if (s.getCurrentMatch().getId().equals(currentGameInfoDto.getGameId())) {
                                return false;
                            }
                        }
                        tempGame.set(currentGameInfoDto);
                        return true;
                    }

                    return false;
                })
                .forEach(s -> {
                    // Get participants from current game and check if other
                    // tracked summoners are in the game. If so, add to skiplist
                    // TODO: dont track custom/practice games
                    CurrentGameInfoDto game = tempGame.get();
                    Set<Summoner> trackedSummonersInGame = filterTrackedSummoners(summonersToCheck,
                            game.getParticipants());

                    // Create match object and save to database
                    Match match = new Match(game.getGameId(), trackedSummonersInGame, Status.IN_PROGRESS, game.getGameQueueConfigId());
                    matchRepo.save(match);

                    logger.info("Found new match {} for {} summoners", match.getId(),
                            match.getTrackedSummoners().size());

                    // Check for notable events
                    checkForNotableEvents(game, trackedSummonersInGame);

                    skiplist.addAll(trackedSummonersInGame);
                });

    }

    private CurrentGameInfoDto getCurrentGame(String summonerId) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
            logger.warn("Thread sleep interrupted!");
        }

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

    private void checkForNotableEvents(CurrentGameInfoDto currentGameInfo, Set<Summoner> trackedSummoners) {
        try {
            Set<MessageEmbed> embeds = new HashSet<MessageEmbed>();

            ExecutorService executor = Executors.newFixedThreadPool(4);

            gameStartNotificationCheckers
                    .forEach(checker -> {
                        executor.execute(() -> {
                            logger.info("Checking for '{}' for {}", checker.getClass().getSimpleName(),
                                    currentGameInfo.getGameId());
                            
                            Optional<GameStartNotificationResult> result = checker.check(currentGameInfo, trackedSummoners);
                            if (result.isPresent()) {
                                embeds.addAll(embedFactory.getEmbeds(result.get()));
                            }
                        });
                    });

            executor.awaitTermination(5L, TimeUnit.SECONDS);

            if (embeds.size() == 0) {
                logger.info("No notable events found for game {}", currentGameInfo.getGameId());
                return;
            }

            // TODO: send webhooks to appropriate guilds
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }

    public Set<Summoner> filterTrackedSummoners(Set<Summoner> trackedSummoners,
            ParticipantDto[] participants) {
        Set<Summoner> trackedSummonersInGame = new HashSet<Summoner>();

        for (Summoner s : trackedSummoners) {
            for (ParticipantDto p : participants) {
                if (s.getId().equals(p.getSummonerId())) {
                    trackedSummonersInGame.add(s);
                }
            }
        }

        return trackedSummonersInGame;
    }

}