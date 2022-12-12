package com.alistats.discorki.notification.team_post_game;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.alistats.discorki.notification.Notification;
import com.alistats.discorki.riot.dto.match.MatchDto;
import com.alistats.discorki.riot.dto.match.ParticipantDto;

@Component
public class TankNotification extends Notification implements TeamPostGameNotification {

    @Override
    public String getName() {
        return "TankNotification";
    }
    @Override
    public String getFancyName() {
        return "SuperSoaker notification";
    }
    @Override
    public String getDescription() {
        return "Notifies when a player takes the most damage in a game.";
    }

    @Override
    public Optional<TeamPostGameNotificationResult> check(MatchDto match, Set<ParticipantDto> trackedParticipants) {
        List<ParticipantDto> participants = Arrays.asList(match.getInfo().getParticipants());
        ParticipantDto superSoaker = Collections.max(participants,
                Comparator.comparing(
                        s -> (s.getTotalDamageTaken() +
                                s.getDamageSelfMitigated())));

        if (trackedParticipants.stream().anyMatch(p -> p.getSummonerName().equals(superSoaker.getSummonerName()))) {
            TeamPostGameNotificationResult result = new TeamPostGameNotificationResult();
            result.setNotification(this);
            result.setMatch(match);
            result.setSubject(superSoaker);
            result.setTitle("SuperSoaker");

            return Optional.of(result);
        }

        return Optional.empty();
    }
}
