package com.alistats.discorki.notification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Component;

import com.alistats.discorki.dto.discord.EmbedDto;
import com.alistats.discorki.dto.discord.ImageDto;
import com.alistats.discorki.dto.discord.ThumbnailDto;
import com.alistats.discorki.dto.riot.match.MatchDto;
import com.alistats.discorki.dto.riot.match.ParticipantDto;
import com.alistats.discorki.util.ColorUtil;

@Component
public class TopDpsNotification extends Notification implements ITeamPostGameNotification {
    @Override
    public ArrayList<EmbedDto> check(MatchDto match, ArrayList<ParticipantDto> trackedParticipants) {

        List<ParticipantDto> participants = Arrays.asList(match.getInfo().getParticipants());
        // Check which summoner got the most damage
        ParticipantDto topDps = Collections.max(participants,
                Comparator.comparing(s -> s.getTotalDamageDealtToChampions()));

        if (!(trackedParticipants.stream().anyMatch(p -> p.getSummonerName().equals(topDps.getSummonerName())))) {
            return new ArrayList<EmbedDto>();
        }
        ArrayList<EmbedDto> embeds = new ArrayList<EmbedDto>();

        embeds.add(buildEmbed(match, topDps));
        return embeds;
    }

    private EmbedDto buildEmbed(MatchDto match, ParticipantDto participant) {
        // Get queue name
        String queueName = gameConstantService.getQueue(match.getInfo().getQueueId()).getDescription();

        // Build description
        HashMap<String, Object> templateData = new HashMap<String, Object>();
        templateData.put("match", match);
        templateData.put("participant", participant);
        templateData.put("queueName", queueName);
        String description = templatingService.renderTemplate("templates/topDpsNotification.md.pebble", templateData);

        // Build embed
        EmbedDto embedDto = new EmbedDto();
        embedDto.setTitle(participant.getSummonerName() + " just got TOP DPS!");
        embedDto.setImage(new ImageDto(imageService.getChampionSplashUrl(participant.getChampionName()).toString()));
        embedDto.setThumbnail(new ThumbnailDto(imageService.getMapUrl(match.getInfo().getMapId()).toString()));
        embedDto.setDescription(description);
        embedDto.setColor(ColorUtil.generateRandomColorFromString(participant.getSummonerName()));

        return embedDto;
    }
}
