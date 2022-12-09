package com.alistats.discorki.notification.common;

import java.util.ArrayList;
import java.util.Set;

import com.alistats.discorki.discord.dto.EmbedDto;
import com.alistats.discorki.riot.dto.match.MatchDto;
import com.alistats.discorki.riot.dto.match.ParticipantDto;

@FunctionalInterface
public interface TeamPostGameNotification {
    ArrayList<EmbedDto> check(MatchDto match, Set<ParticipantDto> participants);
}