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
import com.alistats.discorki.model.Summoner;
import com.alistats.discorki.util.ColorUtil;

@Component
public class TopDpsNotification extends PostGameNotification implements IPostGameNotification{
    @Override
    public ArrayList<EmbedDto> check(MatchDto match) {
        // Find summoner in participants
        List<ParticipantDto> participants = Arrays.asList(match.getInfo().getParticipants());

        // Check which summoner got the most damage
        ParticipantDto topDps = Collections.max(participants, Comparator.comparing(s -> s.getTotalDamageDealtToChampions()));

        // Check if summoner is tracked
        ArrayList<Summoner> summoners = summonerRepo.findByIsTracked(true).get();
        ArrayList<EmbedDto> embeds = new ArrayList<EmbedDto>();
        // TODO: check if it doesn't make more sense to check this:
        //       Summoner trackedSummoner = summonerRepo.findByPuuid(topDps.getPuuid());
        //       if (trackedSummoner != null) {
        //          return new ArrayList<EmbedDto>(Arrays.asList(buildEmbed(match, topDps, trackedSummoner)));
        //       }
        // as it will only fetch a single (or zero) summoner from the database, eliminating the need for a loop.
        // Edge case: bot without Puuid did most damage, so return early?
        for (Summoner summoner : summoners) {
            if (summoner.getPuuid().equals(topDps.getPuuid())) {
                embeds.add(buildEmbed(match, topDps, summoner));
                return embeds;
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
        String description = templatingService.renderTemplate("templates/topDpsNotification.md.pebble", templateData);

        // Build embed
        EmbedDto embedDto = new EmbedDto();
        embedDto.setTitle(summoner.getName() + "just got TOP DPS!");
        embedDto.setImage(new ImageDto(imageService.getChampionSplashUrl(participant.getChampionName()).toString()));
        embedDto.setThumbnail(new ThumbnailDto(imageService.getMapUrl(match.getInfo().getMapId()).toString()));
        embedDto.setDescription(description);
        embedDto.setColor(ColorUtil.generateRandomColorFromString(summoner.getName()));

        return embedDto;
    }
}
