package com.alistats.discorki.notification;

import java.util.ArrayList;

import com.alistats.discorki.dto.discord.EmbedDto;
import com.alistats.discorki.dto.riot.match.MatchDto;

@FunctionalInterface
public interface IPostGameNotification {
    ArrayList<EmbedDto> check(MatchDto match);
}