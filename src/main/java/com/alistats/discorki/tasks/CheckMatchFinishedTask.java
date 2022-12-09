package com.alistats.discorki.tasks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alistats.discorki.discord.dto.EmbedDto;
import com.alistats.discorki.discord.dto.WebhookDto;
import com.alistats.discorki.model.Match;
import com.alistats.discorki.model.Match.Status;
import com.alistats.discorki.model.Rank;
import com.alistats.discorki.model.Summoner;
import com.alistats.discorki.notification.common.PersonalPostGameNotification;
import com.alistats.discorki.notification.common.TeamPostGameNotification;
import com.alistats.discorki.riot.dto.league.LeagueEntryDto;
import com.alistats.discorki.riot.dto.match.MatchDto;
import com.alistats.discorki.riot.dto.match.ParticipantDto;

@Component
public final class CheckMatchFinishedTask extends Task {
    @Autowired
    private List<TeamPostGameNotification> teamNotificationCheckers;
    @Autowired
    private List<PersonalPostGameNotification> personalNotificationCheckers;

    // Run every minute at second :30
    @Scheduled(cron = "30 * * 1/1 * ?")
    public void checkMatchFinished() throws RuntimeException {
        logger.info("Running task {}", this.getClass().getSimpleName());

        // Get all matches in progress
        ArrayList<Match> matchesInProgress = matchRepo.findByStatus(Status.IN_PROGRESS).orElseThrow();

        // Check if the games are finished
        for (Match match : matchesInProgress) {
            logger.info("Checking if game {} is finished...", match.getId());
            Set<Summoner> summoners = match.getTrackedSummoners();
            try {
                MatchDto matchDto = leagueApiController.getMatch(match.getId());
                if (matchDto.isAborted()) {
                    logger.info("Game {} was aborted, not checking for events!", match.getId());
                } else {
                    logger.info("Game {} is finished, checking for notable events...", match.getId());
                    checkForNotableEvents(matchDto, summoners);
                }
                logger.debug("Setting {} to finished.", match.getId());
                match.setStatus(Status.FINISHED);
                matchRepo.save(match);

            } catch (Exception e) {
                if (e.getMessage().contains("404")) {
                    logger.debug("Game {} is not finished yet.", match.getId());
                } else {
                    logger.error("Error while checking if game {} is finished. {}", match.getId(), e.getMessage());
                }
            }           
        }
    }

    private void checkForNotableEvents(MatchDto match, Set<Summoner> trackedParticipatingSummoners) {
        try {            
            Set<ParticipantDto> trackedParticipants = filterTrackedParticipants(trackedParticipatingSummoners,
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
                    logger.debug("Checking for '{}' for {}", checker.getClass().getSimpleName(), match.getInfo().getGameId());
                    embeds.addAll(checker.check(match, trackedParticipants));
                });
            });
            // Wait for all threads to finish
            executor.awaitTermination(10L, TimeUnit.SECONDS);

            if (embeds.size() == 0) {
                logger.info("No notable events found for {}", match.getInfo().getGameId());
                return;
            }

            // Send embeds to discord
            logger.info("Sending webhook to Discord.");
            HashMap<ParticipantDto, Rank> participantRanks = getParticipantRanks(
                    match.getInfo().getParticipants());
            embeds.add(webhookBuilder.buildMatchEmbed(match, participantRanks));
            WebhookDto webhookDto = webhookBuilder.build(embeds);

            discordController.send(webhookDto);

        } catch (Exception e) {
            logger.error(e.getMessage());
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

    public Set<ParticipantDto> filterTrackedParticipants(Set<Summoner> trackedSummoners,
            ParticipantDto[] participants) {
        return Arrays.stream(participants)
                .filter(p -> trackedSummoners.stream().anyMatch(s -> s.getPuuid().equals(p.getPuuid())))
                .collect(Collectors.toCollection(HashSet::new));
    }

}
