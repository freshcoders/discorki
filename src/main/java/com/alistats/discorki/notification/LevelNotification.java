package com.alistats.discorki.notification;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.stereotype.Component;

import com.alistats.discorki.discord.dto.EmbedDto;
import com.alistats.discorki.riot.dto.match.MatchDto;
import com.alistats.discorki.model.Summoner;
import com.alistats.discorki.notification.common.PersonalPostGameNotification;
import com.alistats.discorki.notification.common.Notification;
import com.alistats.discorki.util.ColorUtil;

@Component
public class LevelNotification extends Notification implements PersonalPostGameNotification {


    @Override
    public ArrayList<EmbedDto> check(MatchDto match, Summoner summoner) {
        // Init embed array
        ArrayList<EmbedDto> embeds = new ArrayList<EmbedDto>();

        // we have the league api controller and we want to use it to findbyPuuid
        Long oldLevel = summoner.getSummonerLevel();
        try {
            Long newLevel = leagueApiController.getSummoner(summoner.getName()).toSummoner().getSummonerLevel();
            if (!checkLevelCondition(oldLevel, newLevel))
            return embeds;
            embeds.add(
                buildEmbed(summoner)
            );
        } catch (Exception e) {
            logger.error("Could not get summoner level for {} because of {}", e.getMessage());
        }
        return embeds;
    }


    
    private boolean checkLevelCondition(Long oldLevel, Long newLevel) {
        if (oldLevel == newLevel) {
            return false;
        }

        boolean centenaryMilestone = (newLevel % 100) == 0;
        boolean devilMilestone = newLevel == 666;
        boolean blazeItMilestone = newLevel == 420;
        boolean funnyNumberMilestone = newLevel == 69;

        return centenaryMilestone || devilMilestone || blazeItMilestone || funnyNumberMilestone;
    }



    private EmbedDto buildEmbed(Summoner summoner) throws IOException {
        // Build description
        HashMap<String, Object> templateData = new HashMap<String, Object>();
        templateData.put("summoner", summoner);
        String description;

        description = templatingService.renderTemplate("templates/notifications/level_milestone.md.pebble", templateData);

        // Build embed
        EmbedDto embedDto = new EmbedDto();
        StringBuilder title = new StringBuilder();
        title.append(summoner.getName() + " has reached level " + summoner.getSummonerLevel() + "!");
        embedDto.setTitle(title.toString());
        embedDto.setDescription(description);
        // If promoted, color is green, if demoted, color is red
        embedDto.setColor(ColorUtil.generateRandomColorFromString(summoner.getName()));

        return embedDto;
    }
}
