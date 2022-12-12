package com.alistats.discorki.notification.game_start;

import java.util.HashMap;
import java.util.Set;

import com.alistats.discorki.model.Summoner;
import com.alistats.discorki.notification.Notification;
import com.alistats.discorki.riot.dto.spectator.CurrentGameInfoDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameStartNotificationResult {
    private Notification notification;
    private Set<Summoner> subjects;
    private CurrentGameInfoDto match;
    private String title;
    private HashMap<String, Object> extraArguments = new HashMap<>();

    public void addExtraArgument(String key, Object value) {
        extraArguments.put(key, value);
    }
}