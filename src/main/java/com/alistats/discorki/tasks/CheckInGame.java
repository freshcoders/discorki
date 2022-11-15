package com.alistats.discorki.tasks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alistats.discorki.controller.LeagueApiController;
import com.alistats.discorki.dto.SummonerDto;

@Component
/**
 * This class is used to check if the user is in game.
 * If they are, we register the game and start tracking it.
 */
public class CheckInGame {
    @Autowired LeagueApiController leagueApiController = new LeagueApiController();
    
    @Scheduled(cron = "* */2 * * * ?")
    public void checkInGame() {
        // Get summoner name
        SummonerDto summoner = leagueApiController.getSummoner("slappez");
        System.out.println(summoner.getPuuid());
    }
}
