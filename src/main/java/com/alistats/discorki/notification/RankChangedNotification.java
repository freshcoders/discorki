package com.alistats.discorki.notification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alistats.discorki.controller.LeagueApiController;
import com.alistats.discorki.dto.discord.EmbedDto;
import com.alistats.discorki.dto.discord.ThumbnailDto;
import com.alistats.discorki.dto.riot.league.LeagueEntryDto;
import com.alistats.discorki.dto.riot.match.MatchDto;
import com.alistats.discorki.dto.riot.match.ParticipantDto;
import com.alistats.discorki.model.Rank;
import com.alistats.discorki.model.Summoner;
import com.alistats.discorki.model.Rank.CompareResult;
import com.alistats.discorki.repository.RankRepo;
import com.alistats.discorki.util.ColorUtil;

@Component
public class RankChangedNotification extends PostGameNotification implements IPostGameNotification {

    @Autowired private RankRepo rankRepo;
    @Autowired private LeagueApiController leagueApiController;

    @Override
    public ArrayList<EmbedDto> check(MatchDto match) {
        ArrayList<EmbedDto> embeds = new ArrayList<EmbedDto>();

        // Check if it was a ranked game
        if (match.getInfo().getQueueId() != 420 && match.getInfo().getQueueId() != 440) {
            return embeds;
        }

        // Get queue type
        // TODO: move magic numbers to other class or something
        String queueType = match.getInfo().getQueueId() == 420 ? "RANKED_SOLO_5x5" : "RANKED_FLEX_SR";

        // Get tracked summoners from database
        ArrayList<Summoner> summoners = summonerRepo.findByIsTracked(true).get();

        // Find summoner in participants
        List<ParticipantDto> participants = Arrays.asList(match.getInfo().getParticipants());

        // Check for tracked summoners if they got a penta
        for (Summoner summoner : summoners) {
            for (ParticipantDto participant : participants) {
                if (participant.getPuuid().equals(summoner.getPuuid())) {
                    // Get latest rank from database
                    if(rankRepo.findLatestBySummonerId(summoner.getId(), queueType).isPresent()) {
                        Rank latestRank = rankRepo.findLatestBySummonerId(summoner.getId(),queueType).get();

                        // Get current ranks
                        try {
                            List<LeagueEntryDto> leagueEntries = Arrays.asList(leagueApiController.getLeagueEntries(summoner.getId()));

                            // Find rank for queue type
                            Rank currentRank = null;
                            for (LeagueEntryDto leagueEntry : leagueEntries) {
                                if (leagueEntry.getQueueType().equals(queueType)) {
                                    currentRank = leagueEntry.toRank(summoner);
                                    break;
                                }
                            }

                            // Compare rank
                            CompareResult compareResult = Rank.compareRankByDivision(latestRank, currentRank);

                            String queueDescription = gameConstantService.getQueue(match.getInfo().getQueueId()).getDescription();

                            switch (compareResult) {
                                case GREATER:
                                    embeds.add(buildEmbed(summoner, currentRank, queueDescription, true));
                                    break;
                                case LESS:
                                    embeds.add(buildEmbed(summoner, currentRank, queueDescription, false));
                                    break;
                                default:
                                    break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            continue;
                        }
                        
                        
                    }
                }
            }
        }

        return embeds;
    }

    private EmbedDto buildEmbed(Summoner summoner, Rank newRank, String queueDescription, boolean isPromotion) {
        // Build description
        HashMap<String, Object> templateData = new HashMap<String, Object>();
        templateData.put("rank", newRank);
        templateData.put("queueDescription", queueDescription);
        String description;
        if (isPromotion) {
            description = templatingService.renderTemplate("templates/promoteNotification.md.pebble", templateData);
        } else {
            description = templatingService.renderTemplate("templates/demoteNotification.md.pebble", templateData);
        }
        
        // Build embed
        EmbedDto embedDto = new EmbedDto();
        // TODO: use stringbuilder
        embedDto.setTitle(summoner.getName() + (isPromotion ? "promoted" : "demoted") + " to " + newRank.getTier() + " " + newRank.getDivision() + "!");
        embedDto.setThumbnail(new ThumbnailDto(imageService.getRankEmblemUrl(newRank.getDivision(), newRank.getTier()).toString()));
        embedDto.setDescription(description);
        embedDto.setColor(ColorUtil.getTierColor(newRank.getTier()));

        return embedDto;
    }
}
