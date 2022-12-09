package com.alistats.discorki.notification.common;

import java.util.ArrayList;

import com.alistats.discorki.discord.dto.EmbedDto;
import com.alistats.discorki.riot.dto.match.MatchDto;
import com.alistats.discorki.model.Summoner;

public interface PersonalPostGameNotification {
    ArrayList<EmbedDto> check(MatchDto match, Summoner summoner);
}