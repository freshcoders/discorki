package com.alistats.discorki.notification.personal_post_game;

import java.util.HashMap;

import com.alistats.discorki.model.Summoner;
import com.alistats.discorki.notification.Notification;
import com.alistats.discorki.riot.dto.match.MatchDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonalPostGameNotificationResult {
    private Notification notification;
    private Summoner subject;
    private MatchDto match;
    private String title;
    private HashMap<String, Object> extraArguments = new HashMap<>();

    public void addExtraArgument(String key, Object value) {
        extraArguments.put(key, value);
    }
}