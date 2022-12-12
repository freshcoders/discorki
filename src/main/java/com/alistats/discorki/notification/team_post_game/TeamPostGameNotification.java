package com.alistats.discorki.notification.team_post_game;

import java.util.HashMap;
import java.util.Optional;

import com.alistats.discorki.model.Summoner;
import com.alistats.discorki.notification.result.TeamPostGameNotificationResult;
import com.alistats.discorki.riot.dto.match.MatchDto;
import com.alistats.discorki.riot.dto.match.ParticipantDto;

@FunctionalInterface
public interface TeamPostGameNotification {
    Optional<TeamPostGameNotificationResult> check(MatchDto match, HashMap<Summoner, ParticipantDto> trackedParticipants);
}