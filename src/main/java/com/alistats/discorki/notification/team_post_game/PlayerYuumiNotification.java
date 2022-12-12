package com.alistats.discorki.notification.team_post_game;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.alistats.discorki.notification.Notification;
import com.alistats.discorki.riot.dto.match.MatchDto;
import com.alistats.discorki.riot.dto.match.ParticipantDto;

@Component
public class PlayerYuumiNotification extends Notification implements TeamPostGameNotification {
    @Override
    public String getName() {
        return "PlayerPlayedYuumiNotification";
    }
    @Override
    public String getFancyName() {
        return "Player played Yuumi notification";
    }
    @Override
    public String getDescription() {
        return "Notifies when a player played Yuumi.";
    }

    @Override
    public Optional<TeamPostGameNotificationResult> check(MatchDto match, Set<ParticipantDto> trackedParticipants) {
        Set <ParticipantDto> subjects = new HashSet<ParticipantDto>();

        // Check for tracked summoners if they got a penta
        for (ParticipantDto participant : trackedParticipants) {
            // Check if participant is yuumi
            if (participant.getChampionName().equals("Yuumi")) {
                subjects.add(participant);
            }
        }

        if (subjects.size() > 0) {
            TeamPostGameNotificationResult result = new TeamPostGameNotificationResult();
            result.setNotification(this);
            result.setMatch(match);
            result.setSubjects(subjects);
            result.setTitle("Yuumi!");

            return Optional.of(result);
        }

        return Optional.empty();
    }
}
