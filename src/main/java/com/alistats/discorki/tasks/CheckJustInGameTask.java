package com.alistats.discorki.tasks;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alistats.discorki.controller.DiscordController;
import com.alistats.discorki.controller.LeagueApiController;
import com.alistats.discorki.dto.discord.EmbedDto;
import com.alistats.discorki.dto.discord.WebhookDto;
import com.alistats.discorki.dto.riot.spectator.CurrentGameInfoDto;
import com.alistats.discorki.model.Summoner;
import com.alistats.discorki.notification.ClashGameStartNotification;
import com.alistats.discorki.repository.SummonerRepo;
import com.alistats.discorki.service.WebhookBuilder;

@Component
/**
 * This class is used to check if the user is in game.
 */
public final class CheckJustInGameTask extends Task{
    @Autowired LeagueApiController leagueApiController;
    @Autowired SummonerRepo summonerRepo;
    @Autowired ClashGameStartNotification clashGameStartNotification;
    @Autowired WebhookBuilder webhookBuilder;
    @Autowired DiscordController discordController;

    // Run every 5 minutes.
    @Scheduled(cron = "0 0/5 * 1/1 * ?")
    public void checkJustInGame() {
        // Get all summoners that are tracked
        for (Summoner summoner : summonerRepo.findByIsTracked(true).get()) {
            logger.debug("Checking if " + summoner.getName() + " is in game.");
            // If summoner not in game, check if in game
            if (!summoner.isInGame()) {
                // If in game, set inGame to true
                try {
                    CurrentGameInfoDto currentGameInfoDto = leagueApiController.getCurrentGameInfo(summoner.getId());
                    if (currentGameInfoDto != null) {
                        summoner.setCurrentGameId(currentGameInfoDto.getGameId());
                        summonerRepo.save(summoner);
                        logger.info("User " + summoner.getName() + " is now in game.");
                        checkForNotableEvents(currentGameInfoDto);
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        }
    }

    private void checkForNotableEvents(CurrentGameInfoDto currentGameInfoDto) {
        try {
            // Get embeds from all PostGameNotifications
            ArrayList<EmbedDto> embeds = new ArrayList<EmbedDto>();
            embeds.add(clashGameStartNotification.check(currentGameInfoDto));
            // Send embeds to discord
            if (embeds.size() > 0) {
                WebhookDto webhookDto = webhookBuilder.build(embeds);
                discordController.sendWebhook(webhookDto);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}