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
import com.alistats.discorki.riot.dto.MatchDto;
import com.alistats.discorki.riot.dto.MatchDto.InfoDto.ParticipantDto;

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
    public Optional<TeamPostGameNotificationResult> check(MatchDto match, HashMap<Summoner, ParticipantDto> trackedParticipants) {
        List<ParticipantDto> participants = Arrays.asList(match.getInfo().getParticipants());
        ParticipantDto maxDamageDealt = Collections.max(participants,
                Comparator.comparing(
                        ParticipantDto::getTotalDamageDealtToChampions));
        
        for (Summoner summoner : trackedParticipants.keySet()) {
            if (trackedParticipants.get(summoner).getSummonerName().equals(maxDamageDealt.getSummonerName())) {
                TeamPostGameNotificationResult result = new TeamPostGameNotificationResult();
                result.setNotification(this);
                result.setMatch(match);
                result.setTitle("Top DPS!");
                HashMap<Summoner, ParticipantDto> subject = new HashMap<>();
                subject.put(summoner, maxDamageDealt);
                result.setSubjects(subject);
                
                return Optional.of(result);
            }
        }

        return Optional.empty();
    }
}
