package com.alistats.discorki.notification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

import org.springframework.stereotype.Component;

import com.alistats.discorki.dto.discord.EmbedDto;
import com.alistats.discorki.dto.discord.ImageDto;
import com.alistats.discorki.dto.discord.ThumbnailDto;
import com.alistats.discorki.dto.riot.match.MatchDto;
import com.alistats.discorki.dto.riot.match.ParticipantDto;
import com.alistats.discorki.dto.riot.match.TeamDto;
import com.alistats.discorki.model.Summoner;
import com.alistats.discorki.util.ColorUtil;

// Check if summoner lost custom or coop vs ai
@Component
public class LostAgainstBotsNotification extends PostGameNotification implements IPostGameNotification {
    @Override
    public ArrayList<EmbedDto> check(Summoner summoner, MatchDto match, ArrayList<ParticipantDto> trackedParticipants) {
        ArrayList<EmbedDto> embeds = new ArrayList<EmbedDto>();

        if (!didAFullBotTeamWin(match))
            return embeds;

        trackedParticipants.forEach(participant ->
            embeds.add(buildEmbed(match, participant))
        );

        return embeds;
    }

    private boolean didAFullBotTeamWin(MatchDto match) {
        List<TeamDto> teams = Arrays.asList(match.getInfo().getTeams());
        return teams.stream()
                .filter(TeamDto::isWin)
                .anyMatch(
                        team -> {
                            List<ParticipantDto> participants = Arrays.asList(match.getInfo().getParticipants());

                            boolean isFullBotTeam = participants.stream()
                                    .filter(p -> p.getTeamId().equals(team.getTeamId()))
                                    .allMatch(p -> p.getPuuid().equals("BOT"));
                            // Assuming here that an empty team qualifies as a "full bot team"
                            return isFullBotTeam;
                        }

                );
    }

    private EmbedDto buildEmbed(MatchDto match, ParticipantDto participant) {
        // Get queue name
        String queueName = gameConstantService.getQueue(match.getInfo().getQueueId()).getDescription();

        // Build description
        HashMap<String, Object> templateData = new HashMap<String, Object>();
        templateData.put("match", match);
        templateData.put("participant", participant);
        templateData.put("queueName", queueName);
        String description = templatingService.renderTemplate("templates/lostAgainstBotsNotification.md.pebble",
                templateData);

        // Build embed
        EmbedDto embedDto = new EmbedDto();
        embedDto.setTitle(participant.getSummonerName() + " just lost against bots!");
        embedDto.setImage(new ImageDto(imageService.getChampionSplashUrl(participant.getChampionName()).toString()));
        embedDto.setThumbnail(new ThumbnailDto(imageService.getMapUrl(match.getInfo().getMapId()).toString()));
        embedDto.setDescription(description);
        embedDto.setColor(ColorUtil.generateRandomColorFromString(participant.getSummonerName()));

        return embedDto;
    }
}
