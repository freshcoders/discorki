package com.alistats.discorki.notification.personal_post_game;

import java.util.Optional;

import com.alistats.discorki.riot.dto.match.MatchDto;
import com.alistats.discorki.model.Summoner;

public interface PersonalPostGameNotification {
    Optional<PersonalPostGameNotificationResult> check(MatchDto match, Summoner summoner);
}