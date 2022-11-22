package com.alistats.discorki.tasks;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alistats.discorki.dto.discord.EmbedDto;
import com.alistats.discorki.dto.discord.WebhookDto;
import com.alistats.discorki.dto.riot.spectator.CurrentGameInfoDto;
import com.alistats.discorki.model.Summoner;
import com.alistats.discorki.notification.ClashGameStartNotification;

@Component
/**
 * This class is used to check if the user is in game.
 */
public final class CheckJustInGameTask extends Task{
    // TODO: move to interface
    @Autowired private ClashGameStartNotification clashGameStartNotification;

    // Run every 5 minutes.
    @Scheduled(cron = "0 0/5 * 1/1 * ?")
    public void checkJustInGame() {
        logger.info("Checking if users are in game.");

        // Get all registered summoners from the database
        summonerRepo.findByIsTracked(true).orElseThrow().parallelStream()
            .filter(s -> !s.isInGame())
            .filter(s -> leagueApiController.getCurrentGameInfo(s.getId()) != null)
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

    private void checkForNotableEvents(Summoner summoner, CurrentGameInfoDto currentGameInfoDto) {
        try {
            // Get embeds from all PostGameNotifications
            ArrayList<EmbedDto> embeds = new ArrayList<EmbedDto>();
            embeds.addAll(clashGameStartNotification.check(currentGameInfoDto));
            // Send embeds to discord
            if (embeds.size() > 0) {
                WebhookDto webhookDto = webhookBuilder.build(embeds);
                discordController.sendWebhook(webhookDto);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }
}