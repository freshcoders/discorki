package com.alistats.discorki.notification.game_start;

import java.util.Optional;
import java.util.Set;

import com.alistats.discorki.model.Summoner;
import com.alistats.discorki.notification.result.GameStartNotificationResult;
import com.alistats.discorki.riot.dto.CurrentGameInfoDto;

public interface GameStartNotification {
    Optional<GameStartNotificationResult> check(CurrentGameInfoDto currentGame, Set<Summoner> summoners);
}
