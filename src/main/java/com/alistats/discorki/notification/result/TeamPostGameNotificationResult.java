package com.alistats.discorki.notification.result;

import java.util.HashMap;

import com.alistats.discorki.model.Summoner;
import com.alistats.discorki.riot.dto.MatchDto;
import com.alistats.discorki.riot.dto.MatchDto.InfoDto.ParticipantDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamPostGameNotificationResult extends Result {
    private HashMap<Summoner, ParticipantDto> subjects;
    private MatchDto match;

    public void addSubject(Summoner summoner, ParticipantDto subject) {
        subjects.put(summoner, subject);
    }
}
