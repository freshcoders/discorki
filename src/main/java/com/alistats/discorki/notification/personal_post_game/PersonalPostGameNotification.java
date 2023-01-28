package com.alistats.discorki.notification.personal_post_game;

import java.util.Optional;

import com.alistats.discorki.model.Summoner;
import com.alistats.discorki.notification.result.PersonalPostGameNotificationResult;
import com.alistats.discorki.riot.dto.MatchDto;

public interface PersonalPostGameNotification {
    Optional<PersonalPostGameNotificationResult> check(MatchDto match, Summoner summoner);
}