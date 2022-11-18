package com.alistats.discorki.notification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
public class LostAgainstBotsNotification extends PostGameNotification implements IPostGameNotification{
    @Override
    public ArrayList<EmbedDto> check(MatchDto match) {
        ArrayList<EmbedDto> embeds = new ArrayList<EmbedDto>();

        if (didAFullBotTeamWin(match)) {
            // Get tracked summoners
            ArrayList<Summoner> summoners = summonerRepo.findByIsTracked(true).get();

            // Check which tracked summoner(s) lost.
            // I think with the fullBotTeamWin check, this it is now guaranteed, so we can remove the lost check.

            // TODO: equivalently to the previous MR, we could now check the full losing team
            // and see if any match tracked summoners in the database.
            // This would only give actual benefit when tracking 1000s of summoners (probably).
            for (Summoner summoner : summoners) {
                for (ParticipantDto participant : match.getInfo().getParticipants()) {
                    if (summoner.getPuuid().equals(participant.getPuuid())) {
                        embeds.add(buildEmbed(match, participant, summoner));
                    }
                }
            }
        }

        return embeds;
    }

    private boolean didAFullBotTeamWin(MatchDto match) {
        // Check if a full bot team won
        List<TeamDto> teams = Arrays.asList(match.getInfo().getTeams());
        for (TeamDto team : teams) {
            if (team.isWin()) {
                List<ParticipantDto> participants = Arrays.asList(match.getInfo().getParticipants());
                boolean isFullBotTeam = participants.stream()
                        .filter(p -> p.getTeamId() == team.getTeamId())
                        .allMatch(p -> p.getParticipantId() == null);
                // assuming here that an empty team qualifies as a "full bot team"
                return isFullBotTeam;
            }
        }

        return false;
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
        String description = templatingService.renderTemplate("templates/lostAgainstBotsNotification.md.pebble", templateData);

        // Build embed
        EmbedDto embedDto = new EmbedDto();
        embedDto.setTitle(summoner.getName() + " just lost against bots!");
        embedDto.setImage(new ImageDto(imageService.getChampionSplashUrl(participant.getChampionName()).toString()));
        embedDto.setThumbnail(new ThumbnailDto(imageService.getMapUrl(match.getInfo().getMapId()).toString()));
        embedDto.setDescription(description);
        embedDto.setColor(ColorUtil.generateRandomColorFromString(summoner.getName()));

        return embedDto;
    }
}
