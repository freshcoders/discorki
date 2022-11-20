package com.alistats.discorki.tasks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alistats.discorki.controller.DiscordController;
import com.alistats.discorki.controller.LeagueApiController;
import com.alistats.discorki.dto.discord.EmbedDto;
import com.alistats.discorki.dto.discord.WebhookDto;
import com.alistats.discorki.dto.riot.match.MatchDto;
import com.alistats.discorki.dto.riot.match.ParticipantDto;
import com.alistats.discorki.model.Summoner;
import com.alistats.discorki.notification.IPostGameNotification;
import com.alistats.discorki.notification.LostAgainstBotsNotification;
import com.alistats.discorki.notification.PentaNotification;
import com.alistats.discorki.notification.RankChangedNotification;
import com.alistats.discorki.notification.TopDpsNotification;
import com.alistats.discorki.repository.SummonerRepo;
import com.alistats.discorki.service.WebhookBuilder;

@Component
public final class CheckJustOutOfGameTask extends Task {
    @Autowired LeagueApiController leagueApiController;
    @Autowired DiscordController discordController;
    @Autowired SummonerRepo summonerRepo;
    @Autowired PentaNotification pentaNotification;
    @Autowired LostAgainstBotsNotification lostAgainstBotsNotification;
    @Autowired TopDpsNotification topDpsNotification;
    @Autowired RankChangedNotification rankChangedNotification;
    @Autowired WebhookBuilder webhookBuilder;

    // Run every minute.
    @Scheduled(cron = "0 0/1 * 1/1 * ?")
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
                }
            );
    }

    private void checkForNotableEvents(Summoner summoner) {
        // Get most recent game
        // TODO: check if game isn't already checked
        // https://github.com/freshcoders/discorki/issues/7
        try {
            String matchId = leagueApiController.getMostRecentMatchId(summoner.getPuuid());
            MatchDto latestMatch = leagueApiController.getMatch(matchId);

            ArrayList<IPostGameNotification> notificationCheckers = new ArrayList<IPostGameNotification>();

            notificationCheckers.add(pentaNotification);
            notificationCheckers.add(topDpsNotification);
            // notificationCheckers.add(lostAgainstBotsNotification);
            notificationCheckers.add(rankChangedNotification);

            ArrayList<Summoner> trackedSummoners = summonerRepo.findByPuuidIn(
                    Arrays.stream(latestMatch.getInfo().getParticipants())
                            .map(p -> p.getPuuid())
                            .collect(Collectors.toList()))
                    .orElseThrow(() -> {
                                throw new RuntimeException("No summoners found for match " + matchId);
                    });
            ArrayList<ParticipantDto> trackedParticipants = new ArrayList<ParticipantDto>(
                    Arrays.asList(latestMatch.getInfo().getParticipants()).stream().filter(
                            p -> trackedSummoners.stream().anyMatch(s -> s.getPuuid().equals(p.getPuuid())))
                    .collect(
                            Collectors.toCollection(ArrayList::new)
                            ));

            // Get embeds from all PostGameNotifications
            ArrayList<EmbedDto> embeds = new ArrayList<EmbedDto>();
                
            Thread[] threads = new Thread[notificationCheckers.size()];
            int i = 0;
            for (IPostGameNotification notif : notificationCheckers) {
                threads[i] = new Thread(() -> {
                    embeds.addAll(notif.check(summoner, latestMatch, trackedParticipants));
                });
                threads[i].start();
                i++;
            }
            
            for (int j = 0; j < threads.length; j++) {
                threads[j].join();
            }

            // Send embeds to discord
            if (embeds.size() > 0) {
                WebhookDto webhookDto = webhookBuilder.build(embeds);
                logger.info("Sending webhook to discord.");
                discordController.sendWebhook(webhookDto);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }

    }
}
