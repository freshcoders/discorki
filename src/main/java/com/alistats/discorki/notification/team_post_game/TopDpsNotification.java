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
public class TopDpsNotification extends Notification implements TeamPostGameNotification {

    @Override
    public String getName() {
        return "TopDpsNotification";
    }
    @Override
    public String getFancyName() {
        return "Top DPS notification";
    }
    @Override
    public String getDescription() {
        return "Notifies when a summoner gets the most damage in a game";
    }

    @Override
    public Optional<TeamPostGameNotificationResult> check(MatchDto match, Set<ParticipantDto> trackedParticipants) {
        List<ParticipantDto> participants = Arrays.asList(match.getInfo().getParticipants());
        // Check which summoner got the most damage
        ParticipantDto topDps = Collections.max(participants,
                Comparator.comparing(s -> s.getTotalDamageDealtToChampions()));

        if (trackedParticipants.stream().anyMatch(p -> p.getSummonerName().equals(topDps.getSummonerName()))) {
            TeamPostGameNotificationResult result = new TeamPostGameNotificationResult();
            result.setNotification(this);
            result.setMatch(match);
            result.setSubject(topDps);

            return Optional.of(result);
        }
        
        return Optional.empty();
    }
}
