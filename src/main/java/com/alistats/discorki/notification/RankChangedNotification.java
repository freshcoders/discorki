package com.alistats.discorki.notification;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alistats.discorki.discord.dto.EmbedDto;
import com.alistats.discorki.discord.dto.ThumbnailDto;
import com.alistats.discorki.riot.dto.league.LeagueEntryDto;
import com.alistats.discorki.riot.dto.match.MatchDto;
import com.alistats.discorki.model.Rank;
import com.alistats.discorki.model.Summoner;
import com.alistats.discorki.notification.common.PersonalPostGameNotification;
import com.alistats.discorki.notification.common.Notification;
import com.alistats.discorki.repository.RankRepo;
import com.alistats.discorki.util.ColorUtil;

@Component
public class RankChangedNotification extends Notification implements PersonalPostGameNotification {

    @Autowired
    private RankRepo rankRepo;

    @Override
    public ArrayList<EmbedDto> check(MatchDto match, Summoner summoner) {
        // Init embed array
        ArrayList<EmbedDto> embeds = new ArrayList<EmbedDto>();

        // Check if it was a ranked game
        if (!match.getInfo().isRanked()) {
            return embeds;
        }

        // Get the ranked queue type
        String rankedQueueType = match.getInfo().getRankedQueueType(match.getInfo().getQueueId());

        // Get latest rank from db
        // TODO: use if present since its not really an error
        Rank latestRank = rankRepo.findFirstBySummonerAndQueueTypeOrderByIdDesc(summoner,
        rankedQueueType).orElseThrow();

        // Get current ranks
        List<LeagueEntryDto> leagueEntries = Arrays
                .asList(leagueApiController.getLeagueEntries(summoner.getId()));

        // Find rank for queue type
        Rank currentRank = null;
        for (LeagueEntryDto leagueEntry : leagueEntries) {
            if (leagueEntry.getQueueType().equals(rankedQueueType)) {
                currentRank = leagueEntry.toRank();
                currentRank.setSummoner(summoner);
                break;
            }
        }

        // Compare rank
        int compareResult = latestRank.compareTo(currentRank);
        String queueDescription = leagueGameConstantsController.getQueue(match.getInfo().getQueueId())
                .getDescription();

        if (compareResult == 0) {
            return embeds;
        }

        // Create embed
        try {
            buildEmbed(summoner, currentRank, queueDescription, compareResult == 1);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return embeds;
    }

    private EmbedDto buildEmbed(Summoner summoner, Rank newRank, String queueDescription, boolean isPromotion) throws IOException {
        // Build description
        HashMap<String, Object> templateData = new HashMap<String, Object>();
        templateData.put("summoner", summoner);
        templateData.put("tier", newRank.getLeague().getTier());
        templateData.put("division", newRank.getLeague().getDivision());
        templateData.put("queueDescription", queueDescription);
        String description;

        if (isPromotion) {
            description = templatingService.renderTemplate("templates/notifications/promote.md.pebble", templateData);
        } else {
            description = templatingService.renderTemplate("templates/notifications/demote.md.pebble", templateData);
        }



        // Build embed
        EmbedDto embedDto = new EmbedDto();
        StringBuilder title = new StringBuilder();
        title.append(summoner.getName())
                .append(isPromotion ? " promoted" : " demoted")
                .append(newRank.getLeague().getTier())
                .append(" ")
                .append(newRank.getLeague().getDivision());
        embedDto.setTitle(title.toString());
        embedDto.setThumbnail(
                new ThumbnailDto(imageService.getRankEmblemUrl(newRank.getLeague().getDivision(), newRank.getLeague().getTier()).toString()));
        embedDto.setDescription(description);
        // If promoted, color is green, if demoted, color is red
        embedDto.setColor(isPromotion ? ColorUtil.GREEN : ColorUtil.RED);

        return embedDto;
    }
}
