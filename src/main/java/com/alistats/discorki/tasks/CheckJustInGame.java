package com.alistats.discorki.tasks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alistats.discorki.controller.LeagueApiController;
import com.alistats.discorki.dto.spectator.CurrentGameInfoDto;
import com.alistats.discorki.model.Summoner;
import com.alistats.discorki.repository.SummonerRepo;

@Component
/**
 * This class is used to check if the user is in game.
 */
public class CheckJustInGame extends Check{
    @Autowired LeagueApiController leagueApiController;
    @Autowired SummonerRepo summonerRepo;

    @Scheduled(cron = "*/10 * * * * *")
    public void checkJustInGame() {
        // Get all summoners that are tracked
        for (Summoner summoner : summonerRepo.findByIsTracked(true).get()) {
            // If summoner not in game, check if in game
            if (!summoner.isInGame()) {
                // If in game, set inGame to true
                CurrentGameInfoDto currentGameInfoDto = leagueApiController.getCurrentGameInfo(summoner.getId());
                if (currentGameInfoDto != null) {
                    summoner.setCurrentGameId(currentGameInfoDto.getGameId());
                    summonerRepo.save(summoner);

                    System.out.println("User " + summoner.getName() + " is now in game.");
                }
            }
        }
    }
}