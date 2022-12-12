package com.alistats.discorki.notification.result;

import com.alistats.discorki.model.Summoner;
import com.alistats.discorki.riot.dto.match.MatchDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonalPostGameNotificationResult extends Result {
    private Summoner subject;
    private MatchDto match;
}