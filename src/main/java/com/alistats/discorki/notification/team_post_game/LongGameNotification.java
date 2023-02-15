package com.alistats.discorki.notification.team_post_game;

import java.util.HashMap;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.alistats.discorki.model.Summoner;
import com.alistats.discorki.notification.Notification;
import com.alistats.discorki.notification.result.TeamPostGameNotificationResult;
import com.alistats.discorki.riot.dto.MatchDto;
import com.alistats.discorki.riot.dto.MatchDto.InfoDto.ParticipantDto;

/**
 * A summoner got a penta in the last game
 */
@Component
public class LongGameNotification extends Notification implements TeamPostGameNotification {
    @Override
    public String getName() {
        return "LongGameNotification";
    }
    @Override
    public String getFancyName() {
        return "Long game notification";
    }
    @Override
    public String getDescription() {
        return "Notifies when a summoner was in a really long game.";
    }

    @Override
    public Optional<TeamPostGameNotificationResult> check(MatchDto match, HashMap<Summoner, ParticipantDto> trackedParticipants) {
        final int LONG_GAME_DURATION = 50 * 60; // 55 minutes

        if (match.getInfo().getGameDuration() > LONG_GAME_DURATION) {
            TeamPostGameNotificationResult result = new TeamPostGameNotificationResult();
            result.setNotification(this);
            result.setMatch(match);
            result.setSubjects(trackedParticipants);
            result.setTitle("Wow, that's a long game!");

            return Optional.of(result);
        }

        return Optional.empty();
    }
}
