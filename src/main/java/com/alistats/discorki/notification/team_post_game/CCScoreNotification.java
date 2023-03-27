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
public class CCScoreNotification extends Notification implements TeamPostGameNotification {

    @Override
    public String getName() {
        return "CCScoreNotification";
    }
    @Override
    public String getFancyName() {
        return "CC score notification";
    }
    @Override
    public String getDescription() {
        return "Notifies when a player had a high CC score in a game";
    }

    public final int TRESHOLD_CCSCORE_PER_MINUTE = 6;

    @Override
    public Optional<TeamPostGameNotificationResult> check(MatchDto match, HashMap<Summoner, ParticipantDto> trackedParticipants) {
        // Get the player with the highest damage taken
        List<ParticipantDto> participants = Arrays.asList(match.getInfo().getParticipants());
        ParticipantDto playerWithHighestCCScore  = Collections.max(participants, Comparator.comparing(ParticipantDto::getTimeCCingOthers));
    
        // If the player has less than 6 CC score per minute, ignore
        long minutesPlayed = match.getInfo().getGameDuration() / 60;
        if (playerWithHighestCCScore.getTimeCCingOthers() / minutesPlayed < TRESHOLD_CCSCORE_PER_MINUTE) {
            return Optional.empty();
        }
    
        // Check if the player is tracked
        Optional<Summoner> trackedPlayer = trackedParticipants.keySet().stream()
                .filter(summoner -> trackedParticipants.get(summoner).getSummonerName().equals(playerWithHighestCCScore.getSummonerName()))
                .findFirst();
    
        if (trackedPlayer.isEmpty()) {
            return Optional.empty();
        }
    
        // Create the notification result
        TeamPostGameNotificationResult result = new TeamPostGameNotificationResult();
        result.setNotification(this);
        result.setMatch(match);
        result.setTitle("CC King!");
        result.addExtraArgument("treshold", TRESHOLD_CCSCORE_PER_MINUTE);
        result.addSubject(trackedPlayer.get(), playerWithHighestCCScore);
        
        return Optional.of(result);
    }
}
