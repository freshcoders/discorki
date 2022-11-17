package com.alistats.discorki.tasks;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alistats.discorki.controller.DiscordController;
import com.alistats.discorki.controller.LeagueApiController;
import com.alistats.discorki.dto.discord.EmbedDto;
import com.alistats.discorki.dto.discord.WebhookDto;
import com.alistats.discorki.model.Summoner;
import com.alistats.discorki.notification.PentaNotification;
import com.alistats.discorki.repository.SummonerRepo;
import com.alistats.discorki.util.DiscordDtoBuilder;

@Component
public final class CheckJustOutOfGameTask extends Task{
    @Autowired LeagueApiController leagueApiController;
    @Autowired DiscordController discordController;
    @Autowired SummonerRepo summonerRepo;
    @Autowired PentaNotification pentaNotification;

    @Scheduled(cron = "*/10 * * * * *")
    public void checkJustOutOfGame() {
        // Get all registered summoners from the database
        // todo: implement stream
        for (Summoner summoner : summonerRepo.findByIsTracked(true).get()) {
            if (summoner.isInGame()) {
                try {
                    if (leagueApiController.getCurrentGameInfo(summoner.getId()) == null) {
                        System.out.println("User " + summoner.getName() + " is no longer in game.");
                        checkForNotableEvents(summoner);

                        summoner.setCurrentGameId(null);
                        summonerRepo.save(summoner);
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        };
    }

    private void checkForNotableEvents(Summoner summoner) {
        // Get most recent game
        // todo: check if game isn't already checked
        try {
            String matchId = leagueApiController.getMostRecentMatchId(summoner.getPuuid());
           
            // Get embeds from all PostGameNotifications
            ArrayList<EmbedDto> embeds = new ArrayList<EmbedDto>();
            embeds.addAll(pentaNotification.check(leagueApiController.getMatch(matchId)));

            // Send embeds to discord
            if (embeds.size() > 0) {
                WebhookDto webhookDto = DiscordDtoBuilder.buildWebhook(embeds);
                discordController.sendWebhook(webhookDto);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

    }
}
