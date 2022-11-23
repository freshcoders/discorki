package com.alistats.discorki.notification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alistats.discorki.dto.discord.EmbedDto;
import com.alistats.discorki.dto.discord.ThumbnailDto;
import com.alistats.discorki.dto.riot.spectator.CurrentGameInfoDto;
import com.alistats.discorki.dto.riot.spectator.ParticipantDto;
import com.alistats.discorki.model.Summoner;
import com.alistats.discorki.repository.SummonerRepo;
import com.alistats.discorki.service.ImageService;
import com.alistats.discorki.util.ColorUtil;

@Component
public class ClashGameStartNotification {
    @Autowired protected SummonerRepo summonerRepo;
    @Autowired protected ImageService imageService;
    
    public ArrayList<EmbedDto> check(CurrentGameInfoDto currentGame) {
        ArrayList<EmbedDto> embeds = new ArrayList<EmbedDto>();

        // Check if the game is a clash game
        if (currentGame.getGameQueueConfigId() != 700) {
            return embeds;
        }

        // Get tracked summoners from database
        Optional<ArrayList<Summoner>> summonersOptional = summonerRepo.findByIsTracked(true);
        ArrayList<Summoner> summoners = summonersOptional.orElseThrow(RuntimeException::new);

        // Find summoner in participants
        List<ParticipantDto> participants = Arrays.asList(currentGame.getParticipants());

        ArrayList<Summoner> summonersInGame = new ArrayList<Summoner>();
        
        // Check for tracked summoners if they are in the current game
        for (Summoner summoner : summoners) {
            for (ParticipantDto participant : participants) {
                if (participant.getSummonerId().equals(summoner.getId())) {
                    summonersInGame.add(summoner);
                }
            }
        }

        if (summonersInGame.size() > 0) {
            embeds.add(buildEmbed(summonersInGame));
        }
        return embeds;
    }

    private EmbedDto buildEmbed(ArrayList<Summoner> summoners) {
        EmbedDto embedDto = new EmbedDto();
        embedDto.setTitle("A clash game just started.");
        embedDto.setThumbnail(new ThumbnailDto(imageService.getMapUrl(11).toString()));
        StringBuilder description = new StringBuilder();
        description.append("The following summoners are in game: ")
            .append(System.lineSeparator());
        for (Summoner summoner : summoners) {
            description.append("+").append(summoner.getName())
                .append(System.lineSeparator());
        }
        embedDto.setDescription(description.toString());
        embedDto.setColor(ColorUtil.generateRandomColorFromString("clash"));

        return embedDto;

    }
}
