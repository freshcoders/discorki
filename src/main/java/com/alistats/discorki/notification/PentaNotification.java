package com.alistats.discorki.notification;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.stereotype.Component;

import com.alistats.discorki.dto.discord.EmbedDto;
import com.alistats.discorki.dto.discord.ImageDto;
import com.alistats.discorki.dto.discord.ThumbnailDto;
import com.alistats.discorki.dto.riot.match.MatchDto;
import com.alistats.discorki.dto.riot.match.ParticipantDto;
import com.alistats.discorki.util.ColorUtil;

/**
 * A summoner got a penta in the last game
 */
@Component
public class PentaNotification extends Notification implements ITeamPostGameNotification {
    @Override
    public ArrayList<EmbedDto> check(MatchDto match, ArrayList<ParticipantDto> trackedParticipants) {

        ArrayList<EmbedDto> embeds = new ArrayList<EmbedDto>();

        // Check for tracked summoners if they got a penta
        for (ParticipantDto participant : trackedParticipants) {
            if (participant.getPentaKills() > 0) {
                embeds.add(buildEmbed(match, participant));
            }
        }

        return embeds;
    }

    private EmbedDto buildEmbed(MatchDto match, ParticipantDto participant) {
        // Get queue name
        String queueName = gameConstantService.getQueue(match.getInfo().getQueueId()).getDescription();

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
        embedDto.setImage(new ImageDto(imageService.getChampionSplashUrl(participant.getChampionName()).toString()));
        embedDto.setThumbnail(new ThumbnailDto(imageService.getMapUrl(match.getInfo().getMapId()).toString()));
        embedDto.setDescription(description);
        embedDto.setColor(ColorUtil.generateRandomColorFromString(participant.getSummonerName()));

        return embedDto;
    }
}
