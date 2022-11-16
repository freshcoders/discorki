package com.alistats.discorki.tasks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alistats.discorki.controller.LeagueApiController;
import com.alistats.discorki.repository.SummonerRepo;

@Component
public class CheckJustOutOfGame {
    @Autowired LeagueApiController leagueApiController;
    @Autowired SummonerRepo summonerRepo;

    @Scheduled(cron = "*/10 * * * * *")
    public void checkJustOutOfGame() {
        // Get all registered summoners from the database
        summonerRepo.findAll().forEach(summoner -> {
            if (summoner.getInGame()) {
                if (leagueApiController.getCurrentGameInfo(summoner.getId()) == null) {
                    summoner.setInGame(false);
                    summonerRepo.save(summoner);

                    System.out.println("User " + summoner.getName() + " is no longer in game.");
                }
            }
        });
    }
}
