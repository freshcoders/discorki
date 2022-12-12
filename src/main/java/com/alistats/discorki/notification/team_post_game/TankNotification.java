package com.alistats.discorki.notification.team_post_game;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.alistats.discorki.model.Summoner;
import com.alistats.discorki.notification.Notification;
import com.alistats.discorki.notification.result.TeamPostGameNotificationResult;
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
    public Optional<TeamPostGameNotificationResult> check(MatchDto match, HashMap<Summoner, ParticipantDto> trackedParticipants) {
        List<ParticipantDto> participants = Arrays.asList(match.getInfo().getParticipants());
        ParticipantDto maxDamageTaken = Collections.max(participants,
                Comparator.comparing(
                        s -> (s.getTotalDamageTaken() +
                                s.getDamageSelfMitigated())));
        
        for (Summoner summoner : trackedParticipants.keySet()) {
            if (trackedParticipants.get(summoner).getSummonerName().equals(maxDamageTaken.getSummonerName())) {
                TeamPostGameNotificationResult result = new TeamPostGameNotificationResult();
                result.setNotification(this);
                result.setMatch(match);
                HashMap<Summoner, ParticipantDto> subject = new HashMap<Summoner, ParticipantDto>();
                subject.put(summoner, maxDamageTaken);
                result.setSubjects(subject);
                return Optional.of(result);
            }
        }

        return Optional.empty();
    }
}
