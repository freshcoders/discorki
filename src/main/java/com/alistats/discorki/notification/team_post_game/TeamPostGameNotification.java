package com.alistats.discorki.notification.team_post_game;

import java.util.Optional;
import java.util.Set;

import com.alistats.discorki.riot.dto.match.MatchDto;
import com.alistats.discorki.riot.dto.match.ParticipantDto;

@FunctionalInterface
public interface TeamPostGameNotification {
    Optional<TeamPostGameNotificationResult> check(MatchDto match, Set<ParticipantDto> participants);
}