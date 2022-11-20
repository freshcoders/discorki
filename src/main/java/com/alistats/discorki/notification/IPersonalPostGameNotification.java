package com.alistats.discorki.notification;

import java.util.ArrayList;

import com.alistats.discorki.dto.discord.EmbedDto;
import com.alistats.discorki.dto.riot.match.MatchDto;
import com.alistats.discorki.model.Summoner;

public interface IPersonalPostGameNotification {
    ArrayList<EmbedDto> check(MatchDto match, Summoner summoner);
}