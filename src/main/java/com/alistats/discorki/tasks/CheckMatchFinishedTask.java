package com.alistats.discorki.tasks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alistats.discorki.dto.discord.EmbedDto;
import com.alistats.discorki.dto.discord.WebhookDto;
import com.alistats.discorki.dto.riot.league.LeagueEntryDto;
import com.alistats.discorki.dto.riot.match.MatchDto;
import com.alistats.discorki.dto.riot.match.ParticipantDto;
import com.alistats.discorki.model.Rank;
import com.alistats.discorki.model.Summoner;
import com.alistats.discorki.notification.common.IPersonalPostGameNotification;
import com.alistats.discorki.notification.common.ITeamPostGameNotification;

@Component
public final class CheckMatchFinishedTask extends Task {
    @Autowired
    private List<ITeamPostGameNotification> teamNotificationCheckers;
    @Autowired
    private List<IPersonalPostGameNotification> personalNotificationCheckers;

    // Run every 30 seconds
    @Scheduled(cron = "0/30 0/1 * 1/1 * ?")
    public void checkMatchFinished() throws RuntimeException {
        // Get all tracked matches
        List<Summoner> summonersInGame = summonerRepo.findByCurrentGameIdNotNull();
        if (summonersInGame.size() == 0) {
            return;
        }

        // Create a map for games linked to tracked summoners
        Map<Long, ArrayList<Summoner>> summonerMap =
            summonersInGame.stream().collect(Collectors.groupingBy(Summoner::getCurrentGameId, Collectors.toCollection(ArrayList::new)));


        // Check if the games are finished
        for (Map.Entry<Long, ArrayList<Summoner>> entry : summonerMap.entrySet()) {
            logger.info("Checking if game {} is finished...", entry.getKey());
            Long gameId = entry.getKey();
            ArrayList<Summoner> summoners = entry.getValue();

            MatchDto match = leagueApiController.getMatch(gameId);
            if (match == null) {
                logger.info("Game {} is not finished", gameId);
                continue;
            }

            logger.info("Game {} is finished, checking for notable events...", gameId);
            checkForNotableEvents(match, summoners);

            // Untrack games
            for (Summoner summoner : summoners) {
                logger.debug("Untracking game {} for summoner {}", gameId, summoner.getName());
                summoner.setCurrentGameId(null);
                summonerRepo.save(summoner);
            }
        }
    }

    private void checkForNotableEvents(MatchDto match, ArrayList<Summoner> trackedParticipatingSummoners) {
        try {            
            ArrayList<ParticipantDto> trackedParticipants = filterTrackedParticipants(trackedParticipatingSummoners,
                    match.getInfo().getParticipants());

            // Get embeds from all PostGameNotifications
            ArrayList<EmbedDto> embeds = new ArrayList<EmbedDto>();

            ExecutorService executor = Executors.newFixedThreadPool(4);

            // For each tracked and participating summoner, check for personal notifications
            for (Summoner summoner : trackedParticipatingSummoners) {
                personalNotificationCheckers
                        .forEach(checker -> {
                            executor.execute(() -> {
                                logger.debug("Checking for '{}'  for {}", checker.getClass().getSimpleName(), summoner.getName());
                                embeds.addAll(checker.check(match, summoner));
                            });
                        });
            }

            // Check for team notifications
            teamNotificationCheckers.forEach(checker -> {
                executor.execute(() -> {
                    logger.debug("Checking for '{}'", checker.getClass().getSimpleName());
                    embeds.addAll(checker.check(match, trackedParticipants));
                });
            });
            // Wait for all threads to finish
            executor.awaitTermination(10L, TimeUnit.SECONDS);

            if (embeds.size() == 0) {
                logger.info("No notable events found for " + match.getInfo().getGameId());
                return;
            }

            // Send embeds to discord
            logger.info("Sending webhook to discord.");
            HashMap<ParticipantDto, Rank> participantRanks = getParticipantRanks(
                    match.getInfo().getParticipants());
            embeds.add(webhookBuilder.buildMatchEmbed(match, participantRanks));
            WebhookDto webhookDto = webhookBuilder.build(embeds);

            discordController.sendWebhook(webhookDto);

        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    private HashMap<ParticipantDto, Rank> getParticipantRanks(ParticipantDto[] participants) {
        HashMap<ParticipantDto, Rank> participantRanks = new HashMap<ParticipantDto, Rank>();

        // fetch all the soloq ranks off all team members
        for (ParticipantDto participant : participants) {
            LeagueEntryDto[] leagueEntries = leagueApiController.getLeagueEntries(participant.getSummonerId());
            if (leagueEntries != null) {
                for (LeagueEntryDto leagueEntry : leagueEntries) {
                    if (leagueEntry.getQueueType().equals("RANKED_SOLO_5x5")) {
                        participantRanks.put(participant, leagueEntry.toRank());
                        break;
                    }
                }
            }
        }

        return participantRanks;
    }

    public ArrayList<ParticipantDto> filterTrackedParticipants(ArrayList<Summoner> trackedSummoners,
            ParticipantDto[] participants) {
        return Arrays.stream(participants)
                .filter(p -> trackedSummoners.stream().anyMatch(s -> s.getPuuid().equals(p.getPuuid())))
                .collect(Collectors.toCollection(ArrayList::new));
    }

}
