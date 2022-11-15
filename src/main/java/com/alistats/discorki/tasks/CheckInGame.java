package com.alistats.discorki.tasks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alistats.discorki.controller.LeagueApiController;
import com.alistats.discorki.model.riot.Summoner;

@Component
public class CheckInGame {
    @Autowired LeagueApiController leagueApiController = new LeagueApiController();
    
    @Scheduled(cron = "* */2 * * * ?")
    public void checkInGame() {
        // Get summoner name
        Summoner summoner = leagueApiController.getSummoner("slappez");
        System.out.println(summoner.getPuuid());
    }
}
