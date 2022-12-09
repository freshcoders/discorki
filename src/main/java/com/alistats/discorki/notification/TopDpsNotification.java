package com.alistats.discorki.notification;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.alistats.discorki.discord.dto.EmbedDto;
import com.alistats.discorki.discord.dto.ThumbnailDto;
import com.alistats.discorki.riot.dto.match.MatchDto;
import com.alistats.discorki.riot.dto.match.ParticipantDto;
import com.alistats.discorki.notification.common.TeamPostGameNotification;
import com.alistats.discorki.notification.common.Notification;
import com.alistats.discorki.util.ColorUtil;

@Component
public class TopDpsNotification extends Notification implements TeamPostGameNotification {
    @Override
    public ArrayList<EmbedDto> check(MatchDto match, Set<ParticipantDto> trackedParticipants) {
        ArrayList<EmbedDto> embeds = new ArrayList<EmbedDto>();

        List<ParticipantDto> participants = Arrays.asList(match.getInfo().getParticipants());
        // Check which summoner got the most damage
        ParticipantDto topDps = Collections.max(participants,
                Comparator.comparing(s -> s.getTotalDamageDealtToChampions()));

        if (trackedParticipants.stream().anyMatch(p -> p.getSummonerName().equals(topDps.getSummonerName()))) {
            try {
                embeds.add(buildEmbed(match, topDps));
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        
        return embeds;
    }

    private EmbedDto buildEmbed(MatchDto match, ParticipantDto participant) throws IOException {
        // Build description
        HashMap<String, Object> templateData = new HashMap<String, Object>();
        templateData.put("match", match);
        templateData.put("participant", participant);
        String description = templatingService.renderTemplate("templates/notifications/top_dps.md.pebble",
                templateData);

        // Build embed
        EmbedDto embedDto = new EmbedDto();
        embedDto.setTitle(participant.getSummonerName() + " just got TOP DPS!");
        embedDto.setThumbnail(
                new ThumbnailDto(imageService.getChampionTileUrl(participant.getChampionName()).toString()));
        embedDto.setDescription(description);
        embedDto.setColor(ColorUtil.generateRandomColorFromString(participant.getSummonerName()));

        return embedDto;
    }
}
