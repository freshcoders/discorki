package com.alistats.discorki.notification.result;

import java.util.HashMap;

import com.alistats.discorki.model.Summoner;
import com.alistats.discorki.riot.dto.match.MatchDto;
import com.alistats.discorki.riot.dto.match.ParticipantDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamPostGameNotificationResult extends Result {
    private HashMap<Summoner, ParticipantDto> subjects = new HashMap<>();
    private MatchDto match;

    public void addSubject(Summoner summoner, ParticipantDto subject) {
        subjects.put(summoner, subject);
    }
}
