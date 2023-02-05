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

    public final int TRESHOLD_DMG_TAKEN_PER_MINUTE = 7000;

    @Override
    public Optional<TeamPostGameNotificationResult> check(MatchDto match, HashMap<Summoner, ParticipantDto> trackedParticipants) {
        List<ParticipantDto> participants = Arrays.asList(match.getInfo().getParticipants());
        ParticipantDto maxDamageTaken = Collections.max(participants,
                Comparator.comparing(
                        s -> (s.getTotalDamageTaken() +
                                s.getDamageSelfMitigated())));

        // Check if max damage taken is above the treshold
        long minutesPlayed = match.getInfo().getGameDuration() / 60;
        int totalDamageSoaked = maxDamageTaken.getTotalDamageTaken() + maxDamageTaken.getDamageSelfMitigated();
        if (totalDamageSoaked / minutesPlayed < TRESHOLD_DMG_TAKEN_PER_MINUTE) {
            return Optional.empty();
        }
        
        for (Summoner summoner : trackedParticipants.keySet()) {
            if (trackedParticipants.get(summoner).getSummonerName().equals(maxDamageTaken.getSummonerName())) {
                TeamPostGameNotificationResult result = new TeamPostGameNotificationResult();
                result.setNotification(this);
                result.setMatch(match);
                result.setTitle("SuperSoaker!");
                HashMap<Summoner, ParticipantDto> subject = new HashMap<>();
                subject.put(summoner, maxDamageTaken);
                
                // Add damage treshold and total damage soaked to extra arguments
                HashMap<String, Object> extraArgs = new HashMap<>();
                extraArgs.put("treshold", TRESHOLD_DMG_TAKEN_PER_MINUTE);
                extraArgs.put("totalDamageSoaked", totalDamageSoaked);
                result.setExtraArguments(extraArgs);

                result.setSubjects(subject);
                return Optional.of(result);
            }
        }

        return Optional.empty();
    }
}
