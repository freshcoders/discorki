package com.alistats.discorki.notification;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.alistats.discorki.discord.dto.EmbedDto;
import com.alistats.discorki.discord.dto.ThumbnailDto;
import com.alistats.discorki.riot.dto.match.MatchDto;
import com.alistats.discorki.riot.dto.match.ParticipantDto;
import com.alistats.discorki.notification.common.TeamPostGameNotification;
import com.alistats.discorki.notification.common.Notification;
import com.alistats.discorki.util.ColorUtil;

/**
 * A summoner got a penta in the last game
 */
@Component
public class PentaNotification extends Notification implements TeamPostGameNotification {
    @Override
    public ArrayList<EmbedDto> check(MatchDto match, Set<ParticipantDto> trackedParticipants) {
        ArrayList<EmbedDto> embeds = new ArrayList<EmbedDto>();

        // Check for tracked summoners if they got a penta
        for (ParticipantDto participant : trackedParticipants) {
            if (participant.getPentaKills() > 0) {
                try {
                    embeds.add(buildEmbed(match, participant));
                } catch (IOException e) {
                    logger.error(e.getMessage());
                } 
            }
        }

        return embeds;
    }

    private EmbedDto buildEmbed(MatchDto match, ParticipantDto participant) throws IOException{
        // Get queue name
        String queueName = leagueGameConstantsController.getQueue(match.getInfo().getQueueId()).getDescription();

        // Build description
        HashMap<String, Object> templateData = new HashMap<String, Object>();
        templateData.put("summoner", participant);
        templateData.put("match", match);
        templateData.put("participant", participant);
        templateData.put("queueName", queueName);
        String description = templatingService.renderTemplate("templates/notifications/penta.md.pebble", templateData);

        // Build embed
        EmbedDto embedDto = new EmbedDto();
        embedDto.setTitle("A pentakill for " + participant.getSummonerName() + "!");
        embedDto.setThumbnail(new ThumbnailDto(imageService.getChampionTileUrl(participant.getChampionName()).toString()));
        embedDto.setDescription(description);
        embedDto.setColor(ColorUtil.generateRandomColorFromString(participant.getSummonerName()));

        return embedDto;
    }
}
