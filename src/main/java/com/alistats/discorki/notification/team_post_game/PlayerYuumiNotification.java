package com.alistats.discorki.notification.team_post_game;

import java.util.HashMap;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.alistats.discorki.model.Summoner;
import com.alistats.discorki.notification.Notification;
import com.alistats.discorki.notification.result.TeamPostGameNotificationResult;
import com.alistats.discorki.riot.dto.MatchDto;
import com.alistats.discorki.riot.dto.MatchDto.InfoDto.ParticipantDto;

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
    public Optional<TeamPostGameNotificationResult> check(MatchDto match, HashMap<Summoner, ParticipantDto> trackedParticipants) {
        HashMap<Summoner, ParticipantDto> subjects = new HashMap<>();
        // Check for tracked summoners if they got a penta
        for (Summoner summoner : trackedParticipants.keySet()) {
            // Check if participant is yuumi
            ParticipantDto participant = trackedParticipants.get(summoner);
            if (participant.getChampionName().equals("Yuumi")) {
                subjects.put(summoner, participant);
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
