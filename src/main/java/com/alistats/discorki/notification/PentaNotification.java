package com.alistats.discorki.notification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alistats.discorki.dto.discord.EmbedDto;
import com.alistats.discorki.dto.discord.ImageDto;
import com.alistats.discorki.dto.riot.match.MatchDto;
import com.alistats.discorki.dto.riot.match.ParticipantDto;
import com.alistats.discorki.model.Summoner;
import com.alistats.discorki.service.ImageService;
import com.alistats.discorki.util.ColorUtil;

/**
 * A summoner got a penta in the last game
 */
@Component
public class PentaNotification extends PostGameNotification implements IPostGameNotification{

    @Autowired private ImageService imageService;

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

    private EmbedDto buildEmbed(MatchDto match,  ParticipantDto participant, Summoner summoner) {
        EmbedDto embedDto = new EmbedDto();
        embedDto.setTitle("A pentakill for " + summoner.getName() + "!");
        System.out.println(imageService.getChampionSplashUrl(participant.getChampionName()).toString());
        embedDto.setImage(new ImageDto(imageService.getChampionSplashUrl(participant.getChampionName()).toString()));
        StringBuilder description = new StringBuilder();
        // todo: move to templating engine
        description .append(summoner.getName())
                    .append(" got a penta kill with **")
                    .append(participant.getChampionName())
                    .append("** in a ")
                    .append(match.getInfo().getGameMode())
                    .append(" game.");
        embedDto.setDescription(description.toString());
        embedDto.setColor(ColorUtil.generateRandomColorFromString(summoner.getName()));

        return embedDto;
    }
}
