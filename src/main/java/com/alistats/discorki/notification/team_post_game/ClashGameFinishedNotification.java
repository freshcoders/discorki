package com.alistats.discorki.notification.team_post_game;

import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.alistats.discorki.notification.Notification;
import com.alistats.discorki.riot.dto.match.MatchDto;
import com.alistats.discorki.riot.dto.match.ParticipantDto;

@Component
public class ClashGameFinishedNotification extends Notification implements TeamPostGameNotification {
    @Override
    public String getName() {
        return "ClashGameFinishedNotification";
    }
    @Override
    public String getFancyName() {
        return "Clash game finished notification";
    }
    @Override
    public String getDescription() {
        return "Notifies when a clash game has finished.";
    }

    private static final Integer CLASH_QUEUE_ID = 700;

    @Override
    public Optional<TeamPostGameNotificationResult> check(MatchDto match, Set<ParticipantDto> trackedParticipants) {
        // Check if the game is a clash game
        if (match.getInfo().getQueueId() == CLASH_QUEUE_ID) {
            TeamPostGameNotificationResult result = new TeamPostGameNotificationResult();
            result.setNotification(this);
            result.setMatch(match);
            result.setSubjects(trackedParticipants);
            result.setTitle("Clash game finished");
            
            return Optional.of(result);
        }

        return Optional.empty();
    }
}
