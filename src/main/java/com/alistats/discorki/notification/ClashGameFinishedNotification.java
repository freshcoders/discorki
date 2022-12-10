package com.alistats.discorki.notification;

import java.util.ArrayList;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.alistats.discorki.discord.dto.EmbedDto;
import com.alistats.discorki.notification.common.Notification;
import com.alistats.discorki.notification.common.TeamPostGameNotification;
import com.alistats.discorki.riot.dto.match.MatchDto;
import com.alistats.discorki.riot.dto.match.ParticipantDto;

@Component
public class ClashGameFinishedNotification extends Notification implements TeamPostGameNotification {

    private static final Integer CLASH_QUEUE_ID = 700;

    @Override
    public ArrayList<EmbedDto> check(MatchDto match, Set<ParticipantDto> trackedParticipants) {
        ArrayList<EmbedDto> embeds = new ArrayList<EmbedDto>();
        
        // Check if the game is a clash game
        if (match.getInfo().getQueueId() == CLASH_QUEUE_ID) {
            embeds.add(buildEmbed(match));
        }

        return embeds;
    }

    private EmbedDto buildEmbed(MatchDto match) {
        // Build description
        String description = "A clash game just finished! Did the boys do well?";

        // Build embed
        EmbedDto embedDto = new EmbedDto();
        embedDto.setTitle("Clash game finished");
        embedDto.setDescription(description);

        return embedDto;
    }
}
