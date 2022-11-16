package com.alistats.discorki.tasks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alistats.discorki.controller.LeagueApiController;
import com.alistats.discorki.model.Summoner;
import com.alistats.discorki.repository.SummonerRepo;

@Component
public final class CheckJustOutOfGameTask extends Task {
    @Autowired
    LeagueApiController leagueApiController;
    @Autowired
    SummonerRepo summonerRepo;

    @Scheduled(cron = "*/10 * * * * *")
    public void checkJustOutOfGame() {
        // Get all registered summoners from the database
        // todo: implement stream
        summonerRepo.findAll().forEach(summoner -> {
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
        });
    }

    private void checkForNotableEvents(Summoner summoner) {
        // Get most recent game
        try {
            String matchId = leagueApiController.getMostRecentMatchId(summoner.getPuuid());
            // Retrieve game data
            System.out.println(matchId);
            leagueApiController.getMatch(matchId);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

    }
}
