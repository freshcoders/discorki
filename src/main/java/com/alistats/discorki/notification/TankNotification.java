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
import com.alistats.discorki.notification.common.Notification;
import com.alistats.discorki.notification.common.TeamPostGameNotification;
import com.alistats.discorki.riot.dto.match.MatchDto;
import com.alistats.discorki.riot.dto.match.ParticipantDto;
import com.alistats.discorki.util.ColorUtil;

@Component
public class TankNotification extends Notification implements TeamPostGameNotification {

    private static final String name = "tank";
    private static final String longName = "SuperSoaker";
    private static final String description = "Notifies you when a tracked summoner is the tankiest player in the game.";

    @Override
    public ArrayList<EmbedDto> check(MatchDto match, Set<ParticipantDto> trackedParticipants) {
        ArrayList<EmbedDto> embeds = new ArrayList<EmbedDto>();

        List<ParticipantDto> participants = Arrays.asList(match.getInfo().getParticipants());
        ParticipantDto superSoaker = Collections.max(participants,
                Comparator.comparing(
                        s -> (s.getTotalDamageTaken() +
                                s.getDamageSelfMitigated())));

        if (trackedParticipants.stream().anyMatch(p -> p.getSummonerName().equals(superSoaker.getSummonerName()))) {
            try {
                embeds.add(buildEmbed(match, superSoaker));
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        
        return embeds;
    }

    private EmbedDto buildEmbed(MatchDto match, ParticipantDto participant) throws IOException {
        // Get queue name
        String queueName = leagueGameConstantsController.getQueue(match.getInfo().getQueueId()).getDescription();

        // Build description
        HashMap<String, Object> templateData = new HashMap<String, Object>();
        templateData.put("match", match);
        templateData.put("participant", participant);
        templateData.put("queueName", queueName);
        String description = templatingService.getNotificationTemplate(name(),
                templateData);

        // Build embed
        EmbedDto embedDto = new EmbedDto();
        embedDto.setTitle("SUPER SOAKER!");
        embedDto.setThumbnail(
                new ThumbnailDto(imageService.getChampionTileUrl(participant.getChampionName()).toString()));
        embedDto.setDescription(description);
        embedDto.setColor(ColorUtil.generateRandomColorFromString(participant.getSummonerName()));

        return embedDto;
    }
}
