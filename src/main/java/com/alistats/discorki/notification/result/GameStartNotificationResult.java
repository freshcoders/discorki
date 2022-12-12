package com.alistats.discorki.notification.result;

import java.util.HashMap;

import com.alistats.discorki.model.Summoner;
import com.alistats.discorki.riot.dto.spectator.CurrentGameInfoDto;
import com.alistats.discorki.riot.dto.spectator.ParticipantDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameStartNotificationResult extends Result {
    private HashMap<Summoner, ParticipantDto> subjects = new HashMap<>();
    private CurrentGameInfoDto match;

    public void addSubject(Summoner summoner, ParticipantDto subject) {
        subjects.put(summoner, subject);
    }

    public void addSubject(HashMap<Summoner, ParticipantDto> subjects) {
        this.subjects.putAll(subjects);
    }
}