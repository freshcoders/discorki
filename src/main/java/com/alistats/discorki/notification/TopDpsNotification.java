package com.alistats.discorki.notification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import com.alistats.discorki.dto.discord.EmbedDto;
import com.alistats.discorki.dto.discord.ImageDto;
import com.alistats.discorki.dto.discord.ThumbnailDto;
import com.alistats.discorki.dto.riot.match.MatchDto;
import com.alistats.discorki.dto.riot.match.ParticipantDto;
import com.alistats.discorki.model.Summoner;
import com.alistats.discorki.util.ColorUtil;

@Component
public class TopDpsNotification extends PostGameNotification implements IPostGameNotification{
    @Override
    public ArrayList<EmbedDto> check(MatchDto match) {
        // Find summoner in participants
        List<ParticipantDto> participants = Arrays.asList(match.getInfo().getParticipants());

        // Check which summoner got the most damage
        ParticipantDto topDps = participants.get(0);
        for (ParticipantDto participant : participants) {
            if (participant.getTotalDamageDealtToChampions() > topDps.getTotalDamageDealtToChampions()) {
                topDps = participant;
            }
        }

        // Check if summoner is tracked
        ArrayList<Summoner> summoners = summonerRepo.findByIsTracked(true).get();
        for (Summoner summoner : summoners) {
            if (summoner.getPuuid().equals(topDps.getPuuid())) {
                return new ArrayList<EmbedDto>(Arrays.asList(buildEmbed(match, topDps, summoner)));
            }
        }

        return null;
    }

    private EmbedDto buildEmbed(MatchDto match, ParticipantDto participant, Summoner summoner) {
        // Get queue name
        String queueName = gameConstantService.getQueue(match.getInfo().getQueueId()).getDescription();

        EmbedDto embedDto = new EmbedDto();
        embedDto.setTitle(summoner.getName() + "just got TOP DPS!");
        embedDto.setImage(new ImageDto(imageService.getChampionSplashUrl(participant.getChampionName()).toString()));
        embedDto.setThumbnail(new ThumbnailDto(imageService.getMapUrl(match.getInfo().getMapId()).toString()));
        StringBuilder description = new StringBuilder();
        description .append(summoner.getName())
                    .append(" just got TOP DPS with **")
                    .append(participant.getChampionName())
                    .append("**. He dealt **")
                    .append(participant.getTotalDamageDealtToChampions())
                    .append("** damage in *")
                    .append(match.getInfo().getGameDuration()/60)
                    .append("* minutes.")
                    .append("He finished the game with a kda of **")
                    .append(participant.getKills())
                    .append("/")
                    .append(participant.getDeaths())
                    .append("/")
                    .append(participant.getAssists())
                    .append("** in a *")
                    .append(queueName)
                    .append("*.");
        embedDto.setDescription(description.toString());
        embedDto.setColor(ColorUtil.generateRandomColorFromString(summoner.getName()));

        return embedDto;
    }
}
