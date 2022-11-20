package com.alistats.discorki.tasks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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
import com.alistats.discorki.dto.riot.summoner.PuuidObject;
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
    @Autowired 
    LeagueApiController leagueApiController;
    @Autowired
    DiscordController discordController;
    @Autowired
    SummonerRepo summonerRepo;
    @Autowired
    PentaNotification pentaNotification;
    @Autowired
    LostAgainstBotsNotification lostAgainstBotsNotification;
    @Autowired
    TopDpsNotification topDpsNotification;
    @Autowired
    RankChangedNotification rankChangedNotification;
    @Autowired
    WebhookBuilder webhookBuilder;

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

            ArrayList<IPostGameNotification> notificationCheckers = new ArrayList<IPostGameNotification>();

            notificationCheckers.add(pentaNotification);
            // notificationCheckers.add(topDpsNotification);
            notificationCheckers.add(lostAgainstBotsNotification);
            notificationCheckers.add(rankChangedNotification);

            ArrayList<Summoner> trackedSummoners = getTrackedSummoners(latestMatch.getInfo().getParticipants());

            ArrayList<ParticipantDto> trackedParticipants = filterTrackedParticipants(trackedSummoners, latestMatch.getInfo().getParticipants());

            // Get embeds from all PostGameNotifications
            ArrayList<EmbedDto> embeds = new ArrayList<EmbedDto>();

            ExecutorService executor = Executors.newFixedThreadPool(4);

            for (IPostGameNotification notif : notificationCheckers) {
                Thread worker = new Thread(() -> {
                    embeds.addAll(notif.check(summoner, latestMatch, trackedParticipants));
                });
                executor.execute(worker);
            }
            executor.awaitTermination(5L, TimeUnit.SECONDS);

            // Send embeds to discord
            if (embeds.size() > 0) {
                WebhookDto webhookDto = webhookBuilder.build(embeds);
                // logger.info("Sending webhook to discord.");
                // check if in prod env
                if (System.getenv("env") == "prod") {
                    discordController.sendWebhook(webhookDto);
                } else {
                    // discordController.sendWebhook(webhookDto);
                    logger.info(webhookDto.toString());
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }

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
