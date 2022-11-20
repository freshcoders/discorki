package com.alistats.discorki.notification;

import java.util.ArrayList;

import com.alistats.discorki.dto.discord.EmbedDto;
import com.alistats.discorki.dto.riot.match.MatchDto;
import com.alistats.discorki.dto.riot.match.ParticipantDto;
import com.alistats.discorki.model.Summoner;

@FunctionalInterface
public interface IPostGameNotification {
    ArrayList<EmbedDto> check(Summoner summoner, MatchDto match, ArrayList<ParticipantDto> participants);
}