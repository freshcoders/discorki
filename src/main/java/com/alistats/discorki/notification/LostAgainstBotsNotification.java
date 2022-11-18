package com.alistats.discorki.notification;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import com.alistats.discorki.dto.discord.EmbedDto;
import com.alistats.discorki.dto.discord.ImageDto;
import com.alistats.discorki.dto.discord.ThumbnailDto;
import com.alistats.discorki.dto.riot.match.MatchDto;
import com.alistats.discorki.dto.riot.match.ParticipantDto;
import com.alistats.discorki.model.Summoner;
import com.alistats.discorki.util.ColorUtil;

// Check if summoner lost custom or coop vs ai
@Component
public class LostAgainstBotsNotification extends PostGameNotification implements IPostGameNotification{
    @Override
    public ArrayList<EmbedDto> check(MatchDto match) {
        // Create embeds
        ArrayList<EmbedDto> embeds = new ArrayList<EmbedDto>();

        if (didABotWin(match)) {
            // Get tracked summoners
            ArrayList<Summoner> summoners = summonerRepo.findByIsTracked(true).get();

            // Check if a summoner was on the losing team
            for (Summoner summoner : summoners) {
                for (ParticipantDto participant : match.getInfo().getParticipants()) {
                    if (summoner.getPuuid().equals(participant.getPuuid()) && !participant.isWin()) {
                        embeds.add(buildEmbed(match, participant, summoner));
                    }
                }
            }
        }

        return embeds;
    }

    private boolean didABotWin(MatchDto match) {
        for (ParticipantDto participant : match.getInfo().getParticipants()) {
            if (participant.getParticipantId() == null && participant.isWin()) {
                return true;
            }
        }
        return false;
    }

    private EmbedDto buildEmbed(MatchDto match, ParticipantDto participant, Summoner summoner) {
        // Get queue name
        String queueName = gameConstantService.getQueue(match.getInfo().getQueueId()).getDescription();

        EmbedDto embedDto = new EmbedDto();
        embedDto.setTitle(summoner.getName() + "just lost against bots!");
        embedDto.setImage(new ImageDto(imageService.getChampionSplashUrl(participant.getChampionName()).toString()));
        embedDto.setThumbnail(new ThumbnailDto(imageService.getMapUrl(match.getInfo().getMapId()).toString()));
        StringBuilder description = new StringBuilder();
        description .append("It's unbelievable but he got it done...")
                    .append(summoner.getName())
                    .append(" just lost against bots in ")
                    .append(queueName)
                    .append("!");
        embedDto.setDescription(description.toString());
        embedDto.setColor(ColorUtil.generateRandomColorFromString(summoner.getName()));

        return embedDto;
    }
}
