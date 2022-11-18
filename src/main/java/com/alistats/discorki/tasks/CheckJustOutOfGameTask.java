package com.alistats.discorki.tasks;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alistats.discorki.controller.DiscordController;
import com.alistats.discorki.controller.LeagueApiController;
import com.alistats.discorki.dto.discord.EmbedDto;
import com.alistats.discorki.dto.discord.WebhookDto;
import com.alistats.discorki.dto.riot.match.MatchDto;
import com.alistats.discorki.model.Summoner;
import com.alistats.discorki.notification.LostAgainstBotsNotification;
import com.alistats.discorki.notification.PentaNotification;
import com.alistats.discorki.notification.TopDpsNotification;
import com.alistats.discorki.repository.SummonerRepo;
import com.alistats.discorki.service.WebhookBuilder;

@Component
public final class CheckJustOutOfGameTask extends Task{
    @Autowired LeagueApiController leagueApiController;
    @Autowired DiscordController discordController;
    @Autowired SummonerRepo summonerRepo;
    @Autowired PentaNotification pentaNotification;
    @Autowired LostAgainstBotsNotification lostAgainstBotsNotification;
    @Autowired TopDpsNotification topDpsNotification;
    @Autowired WebhookBuilder webhookBuilder;

    // Run every minute.
    @Scheduled(cron = "0 0/1 * 1/1 * ?")
    public void checkJustOutOfGame() {
        // Get all registered summoners from the database
        // TODO: implement stream
        for (Summoner summoner : summonerRepo.findByIsTracked(true).get()) {
            if (summoner.isInGame()) {
                try {
                    if (leagueApiController.getCurrentGameInfo(summoner.getId()) == null) {
                        logger.info("User " + summoner.getName() + " is no longer in game.");
                        checkForNotableEvents(summoner);

                        summoner.setCurrentGameId(null);
                        summonerRepo.save(summoner);
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage());
                    e.printStackTrace();
                }
            }
        };
    }

    private void checkForNotableEvents(Summoner summoner) {
        // Get most recent game
        // TODO: check if game isn't already checked
        // https://github.com/freshcoders/discorki/issues/7
        try {
            String matchId = leagueApiController.getMostRecentMatchId(summoner.getPuuid());
            MatchDto latestMatch = leagueApiController.getMatch(matchId);

            // Get embeds from all PostGameNotifications
            ArrayList<EmbedDto> embeds = new ArrayList<EmbedDto>();
            embeds.addAll(pentaNotification.check(latestMatch));
            embeds.addAll(lostAgainstBotsNotification.check(latestMatch));
            embeds.addAll(topDpsNotification.check(latestMatch));

            // Send embeds to discord
            if (embeds.size() > 0) {
                WebhookDto webhookDto = webhookBuilder.build(embeds);
                discordController.sendWebhook(webhookDto);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }

    }
}
