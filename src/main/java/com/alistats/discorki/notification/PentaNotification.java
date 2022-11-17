package com.alistats.discorki.notification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alistats.discorki.dto.discord.EmbedDto;
import com.alistats.discorki.dto.discord.ImageDto;
import com.alistats.discorki.dto.discord.ThumbnailDto;
import com.alistats.discorki.dto.riot.match.MatchDto;
import com.alistats.discorki.dto.riot.match.ParticipantDto;
import com.alistats.discorki.model.Summoner;
import com.alistats.discorki.service.GameConstantService;
import com.alistats.discorki.service.ImageService;
import com.alistats.discorki.util.ColorUtil;

/**
 * A summoner got a penta in the last game
 */
@Component
public class PentaNotification extends PostGameNotification implements IPostGameNotification{

    @Autowired private ImageService imageService;
    @Autowired private GameConstantService gameConstantService;

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
                    if (participant.getPentaKills() == 0) {
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

        EmbedDto embedDto = new EmbedDto();
        embedDto.setTitle("A pentakill for " + summoner.getName() + "!");
        embedDto.setImage(new ImageDto(imageService.getChampionSplashUrl(participant.getChampionName()).toString()));
        embedDto.setThumbnail(new ThumbnailDto(imageService.getMapUrl(match.getInfo().getMapId()).toString()));
        StringBuilder description = new StringBuilder();
        // todo: move to templating engine
        // todo: add penta count this season
        description .append(summoner.getName())
                    .append(" got a penta kill with **")
                    .append(participant.getChampionName())
                    .append("** in a *")
                    .append(queueName)
                    .append("*.");
        embedDto.setDescription(description.toString());
        embedDto.setColor(ColorUtil.generateRandomColorFromString(summoner.getName()));

        return embedDto;
    }
}
