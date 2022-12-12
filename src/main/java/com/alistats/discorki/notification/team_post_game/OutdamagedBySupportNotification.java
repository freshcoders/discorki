package com.alistats.discorki.notification.team_post_game;

import java.util.HashMap;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.alistats.discorki.model.Summoner;
import com.alistats.discorki.notification.Notification;
import com.alistats.discorki.notification.result.TeamPostGameNotificationResult;
import com.alistats.discorki.riot.dto.match.MatchDto;
import com.alistats.discorki.riot.dto.match.ParticipantDto;

@Component
public class OutdamagedBySupportNotification extends Notification implements TeamPostGameNotification {
    @Override
    public String getName() {
        return "OutdamagedBySupportNotification";
    }
    @Override
    public String getFancyName() {
        return "Outdamaged by support notification";
    }
    @Override
    public String getDescription() {
        return "Notifies when you are outdamaged by your support.";
    }

    @Override
    public Optional<TeamPostGameNotificationResult> check(MatchDto match, HashMap<Summoner, ParticipantDto> trackedParticipants) {
        // Since we COULD potentially have tracked players on opposite teams:
        // one being support on red, one being adc on blue, we SHOULD check
        // on team-basis. But this is extremely unlikely. For now, we can
        // just check on role and assume the tracked participants are on the same team.
        // After, we check team-equality, so we only have false negatives, no false
        // positives
        ParticipantDto support = null;
        ParticipantDto adc = null;

        // Loop through tracked participants
        for (ParticipantDto participant : trackedParticipants.values()) {
            // Check if participant is adc
            if (participant.getTeamPosition().equals("BOTTOM")) {
                // Get participant playing support on that team
                adc = participant;
            }
            // Check if participant is support
            else if (participant.getTeamPosition().equals("SUPPORT")) {
                // Get participant playing adc on that team
                support = participant;
            }
        }

        // Support, or adc, or both are not tracked.
        if (support == null || adc == null || !support.getTeamId().equals(adc.getTeamId()))
            return Optional.empty();

        // Check if support did more damage than adc
        if (support.getTotalDamageDealtToChampions() > adc.getTotalDamageDealtToChampions()) {
            TeamPostGameNotificationResult result = new TeamPostGameNotificationResult();
            result.setNotification(this);
            result.setMatch(match);
            result.setSubjects(trackedParticipants);
            result.setTitle("Outdamaged by support!");
            result.addExtraArgument("adc", adc);
            result.addExtraArgument("support", support);

            return Optional.of(result);
        }
        
        return Optional.empty();
    }
}
