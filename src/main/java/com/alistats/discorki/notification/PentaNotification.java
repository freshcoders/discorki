package com.alistats.discorki.notification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Component;

import com.alistats.discorki.dto.discord.EmbedDto;
import com.alistats.discorki.dto.discord.ImageDto;
import com.alistats.discorki.dto.discord.ThumbnailDto;
import com.alistats.discorki.dto.riot.match.MatchDto;
import com.alistats.discorki.dto.riot.match.ParticipantDto;
import com.alistats.discorki.model.Summoner;
import com.alistats.discorki.util.ColorUtil;

/**
 * A summoner got a penta in the last game
 */
@Component
public class PentaNotification extends PostGameNotification implements IPostGameNotification{
    @Override
    public ArrayList<EmbedDto> check(MatchDto match) {
        // Get tracked summoners from database
        ArrayList<Summoner> summoners = summonerRepo.findByIsTracked(true).get();

        // Find summoner in participants
        List<ParticipantDto> participants = Arrays.asList(match.getInfo().getParticipants());

        ArrayList<EmbedDto> embeds = new ArrayList<EmbedDto>();
        
        // Check for tracked summoners if they got a penta
        for (Summoner summoner : summoners) {
            for (ParticipantDto participant : participants) {
                if (participant.getPuuid().equals(summoner.getPuuid())) {
                    // TODO: should be greater than 0
                    if (participant.getPentaKills() > 0) {
                        embeds.add(buildEmbed(match, participant, summoner));
                    }
                }
            }
        }

        return embeds;
    }

    private EmbedDto buildEmbed(MatchDto match, ParticipantDto participant, Summoner summoner) {
        // Get queue name
        String queueName = gameConstantService.getQueue(match.getInfo().getQueueId()).getDescription();

        // Build description
        HashMap<String, Object> templateData = new HashMap<String, Object>();
        templateData.put("summoner", summoner);
        templateData.put("match", match);
        templateData.put("participant", participant);
        templateData.put("queueName", queueName);
        String description = templatingService.renderTemplate("templates/pentaNotification.md.pebble", templateData);
        
        // Build embed
        EmbedDto embedDto = new EmbedDto();
        embedDto.setTitle("A pentakill for " + summoner.getName() + "!");
        embedDto.setImage(new ImageDto(imageService.getChampionSplashUrl(participant.getChampionName()).toString()));
        embedDto.setThumbnail(new ThumbnailDto(imageService.getMapUrl(match.getInfo().getMapId()).toString()));
        embedDto.setDescription(description);
        embedDto.setColor(ColorUtil.generateRandomColorFromString(summoner.getName()));

        return embedDto;
    }
}
