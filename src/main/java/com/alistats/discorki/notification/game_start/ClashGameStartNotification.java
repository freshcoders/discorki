package com.alistats.discorki.notification.game_start;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.alistats.discorki.model.Summoner;
import com.alistats.discorki.notification.Notification;
import com.alistats.discorki.riot.dto.spectator.CurrentGameInfoDto;
import com.alistats.discorki.riot.dto.spectator.ParticipantDto;

@Component
public class ClashGameStartNotification extends Notification implements GameStartNotification {
    @Override
    public String getName() {
        return "ClasshGameStartNotification";
    }
    @Override
    public String getFancyName() {
        return "Clash game start notification";
    }
    @Override
    public String getDescription() {
        return "Notifies when a clash game starts.";
    }

    public Optional<GameStartNotificationResult> check(CurrentGameInfoDto currentGame, Set<Summoner> summoners) {
        // Check if the game is a clash game
        if (currentGame.getGameQueueConfigId() != 700) {
            return Optional.empty();
        }

        // Find summoner in participants
        List<ParticipantDto> participants = Arrays.asList(currentGame.getParticipants());

        ArrayList<Summoner> summonersInGame = new ArrayList<Summoner>();
        
        // Check for tracked summoners if they are in the current game
        for (Summoner summoner : summoners) {
            for (ParticipantDto participant : participants) {
                if (participant.getSummonerId().equals(summoner.getId())) {
                    summonersInGame.add(summoner);
                }
            }
        }

        if (summonersInGame.size() > 0) {
            GameStartNotificationResult result = new GameStartNotificationResult();
            result.setNotification(this);
            result.setSubjects(new HashSet<Summoner>(summonersInGame));
            result.setMatch(currentGame);

            return Optional.of(result);
        }
        
        return Optional.empty();
    }
}
