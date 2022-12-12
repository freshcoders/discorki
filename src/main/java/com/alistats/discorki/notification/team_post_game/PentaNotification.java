package com.alistats.discorki.notification.team_post_game;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.alistats.discorki.riot.dto.match.MatchDto;
import com.alistats.discorki.riot.dto.match.ParticipantDto;
import com.alistats.discorki.notification.Notification;

/**
 * A summoner got a penta in the last game
 */
@Component
public class PentaNotification extends Notification implements TeamPostGameNotification {
    @Override
    public String getName() {
        return "PentaNotification";
    }
    @Override
    public String getFancyName() {
        return "Penta notification";
    }
    @Override
    public String getDescription() {
        return "Notifies when a summoner got a penta in the last game.";
    }

    @Override
    public Optional<TeamPostGameNotificationResult> check(MatchDto match, Set<ParticipantDto> trackedParticipants) {
        // Check for tracked summoners if they got a penta
        Set<ParticipantDto> subjects = new HashSet<ParticipantDto>();

        for (ParticipantDto participant : trackedParticipants) {
            if (participant.getPentaKills() > 0) {
                subjects.add(participant);
            }
        }

        if (subjects.size() > 0) {
            TeamPostGameNotificationResult result = new TeamPostGameNotificationResult();
            result.setNotification(this);
            result.setMatch(match);
            result.setSubjects(subjects);
            result.setTitle("Penta!");

            return Optional.of(result);
        }

        return Optional.empty();
    }
}
