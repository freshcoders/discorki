package com.alistats.discorki.tasks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import com.alistats.discorki.dto.riot.league.LeagueEntryDto;
import com.alistats.discorki.dto.riot.match.MatchDto;
import com.alistats.discorki.dto.riot.match.ParticipantDto;
import com.alistats.discorki.dto.riot.summoner.PuuidObject;
import com.alistats.discorki.model.Rank;
import com.alistats.discorki.model.Summoner;
import com.alistats.discorki.notification.IPersonalPostGameNotification;
import com.alistats.discorki.notification.ITeamPostGameNotification;

@Component
public final class CheckJustOutOfGameTask extends Task {
    @Autowired    private List<ITeamPostGameNotification> teamNotificationCheckers;
    @Autowired    private List<IPersonalPostGameNotification> personalNotificationCheckers;

    // Run every minute.
    @Scheduled(cron = "0/5 0/1 * 1/1 * ?")
    public void checkJustOutOfGame() throws RuntimeException {

        // Get all registered summoners from the database
        summonerRepo.findByIsTracked(true).orElseThrow().stream()
                .filter(s -> s.isInGame())
                .filter(s -> leagueApiController.getCurrentGameInfo(s.getId()) == null)
                .forEach(summoner -> {
                    logger.info("User " + summoner.getName() + " is no longer in game.");
                    checkForNotableEvents(summoner);

                    summoner.setCurrentGameId(null);
                    summonerRepo.save(summoner);
                });
    }

    private void checkForNotableEvents(Summoner summoner) {
        // Get most recent game
        // TODO: check if game isn't already checked
        // https://github.com/freshcoders/discorki/issues/7
        try {
            String matchId = leagueApiController.getMostRecentMatchId(summoner.getPuuid());
            MatchDto latestMatch = leagueApiController.getMatch(matchId);

            ArrayList<Summoner> trackedSummoners = getTrackedSummoners(latestMatch.getInfo().getParticipants());

            ArrayList<ParticipantDto> trackedParticipants = filterTrackedParticipants(trackedSummoners, latestMatch.getInfo().getParticipants());

            // Get embeds from all PostGameNotifications
            ArrayList<EmbedDto> embeds = new ArrayList<EmbedDto>();

            ExecutorService executor = Executors.newFixedThreadPool(4);

            personalNotificationCheckers
            .forEach(checker -> {
                executor.execute(() -> {
                    embeds.addAll(checker.check(latestMatch, summoner));
                });
            });
            
            teamNotificationCheckers.forEach(checker -> {
                executor.execute(() -> {
                    embeds.addAll(checker.check(latestMatch, trackedParticipants));
                });
            });

            executor.awaitTermination(5L, TimeUnit.SECONDS);

            if (embeds.size() == 0) {
                logger.info("No notable events found for " + summoner.getName());
                return;
            }

            // Send embeds to discord
            logger.info("Sending webhook to discord.");
            HashMap<ParticipantDto, Rank> participantRanks = getParticipantRanks(latestMatch.getInfo().getParticipants());
            embeds.add(webhookBuilder.buildMatchEmbed(latestMatch, participantRanks));
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
        for(ParticipantDto participant : participants) {
            LeagueEntryDto[] leagueEntries = leagueApiController.getLeagueEntries(participant.getSummonerId());
            if(leagueEntries != null) {
                for(LeagueEntryDto leagueEntry : leagueEntries) {
                    if(leagueEntry.getQueueType().equals("RANKED_SOLO_5x5")) {
                        participantRanks.put(participant, leagueEntry.toRank());
                        break;
                    }
                }
            }
        }

        return participantRanks;
    }

    /**
     * Filter a list of particpants based on puuid and return a list of summoners
     * from the database.
     * 
     * @param puuidObjects
     * @return
     */
    public ArrayList<Summoner> getTrackedSummoners(PuuidObject[] puuidObjects) {

        return summonerRepo.findByPuuidIn(
                Arrays.stream(puuidObjects)
                        .map(p -> p.getPuuid())
                        .collect(Collectors.toList()))
                .orElseThrow(() -> {
                    throw new RuntimeException("No summoners found for match");
                })
                .stream()
                .filter(s -> s.getIsTracked())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<ParticipantDto> filterTrackedParticipants(ArrayList<Summoner> trackedSummoners, ParticipantDto[] participants) {
        return Arrays.stream(participants)
                .filter(p -> trackedSummoners.stream().anyMatch(s -> s.getPuuid().equals(p.getPuuid())))
                .collect(Collectors.toCollection(ArrayList::new));           
    }

}
