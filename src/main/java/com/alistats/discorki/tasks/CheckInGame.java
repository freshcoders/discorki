package com.alistats.discorki.tasks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alistats.discorki.controller.LeagueApiController;
import com.alistats.discorki.repository.SummonerRepo;

@Component
/**
 * This class is used to check if the user is in game.
 */
public class CheckInGame {
    @Autowired LeagueApiController leagueApiController;
    @Autowired SummonerRepo summonerRepo;

    @Scheduled(cron = "*/10 * * * * *")
    public void checkInGame() {
        // Get all registered summoners from the database
        summonerRepo.findAll().forEach(summoner -> {
            // If summoner not in game, check if in game
            if (!summoner.getInGame()) {
                // If in game, set inGame to true
                if (leagueApiController.getCurrentGameInfo(summoner.getId()) != null) {
                    summoner.setInGame(true);
                    summonerRepo.save(summoner);
                }
            }
        });
    }
}
