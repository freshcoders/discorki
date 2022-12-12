package com.alistats.discorki.notification.team_post_game;

import java.util.HashMap;
import java.util.Set;

import com.alistats.discorki.notification.Notification;
import com.alistats.discorki.riot.dto.match.MatchDto;
import com.alistats.discorki.riot.dto.match.ParticipantDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamPostGameNotificationResult {
    private Notification notification;
    private Set<ParticipantDto> subjects;
    private MatchDto match;
    private String title;
    private HashMap<String, Object> extraArguments = new HashMap<>();

    public void addExtraArgument(String key, Object value) {
        extraArguments.put(key, value);
    }

    public void setSubject(ParticipantDto subject) {
        subjects.add(subject);
    }
}
