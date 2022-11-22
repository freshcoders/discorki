package com.alistats.discorki.notification.common;

import java.util.ArrayList;

import com.alistats.discorki.dto.discord.EmbedDto;
import com.alistats.discorki.dto.riot.match.MatchDto;
import com.alistats.discorki.dto.riot.match.ParticipantDto;

@FunctionalInterface
public interface ITeamPostGameNotification {
    ArrayList<EmbedDto> check(MatchDto match, ArrayList<ParticipantDto> participants);
}