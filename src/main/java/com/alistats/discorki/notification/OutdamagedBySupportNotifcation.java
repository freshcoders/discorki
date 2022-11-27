package com.alistats.discorki.notification;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.stereotype.Component;

import com.alistats.discorki.dto.discord.EmbedDto;
import com.alistats.discorki.dto.discord.ThumbnailDto;
import com.alistats.discorki.dto.riot.match.MatchDto;
import com.alistats.discorki.dto.riot.match.ParticipantDto;
import com.alistats.discorki.notification.common.ITeamPostGameNotification;
import com.alistats.discorki.notification.common.Notification;
import com.alistats.discorki.util.ColorUtil;

@Component
public class OutdamagedBySupportNotifcation extends Notification implements ITeamPostGameNotification {
    @Override
    public ArrayList<EmbedDto> check(MatchDto match, ArrayList<ParticipantDto> trackedParticipants) {

        ArrayList<EmbedDto> embeds = new ArrayList<EmbedDto>();

        // Check for tracked summoners if they got a penta
        for (ParticipantDto participant : trackedParticipants) {
            // Check if participant is adc
            if (participant.getTeamPosition() == "BOTTOM") {
                // Get participant playing support on that team
                ParticipantDto support = null;
                for (ParticipantDto p : match.getInfo().getParticipants()) {
                    if (p.getTeamId() == participant.getTeamId() && p.getTeamPosition() == "SUPPORT") {
                        // Check if support did more damage than adc
                        if (p.getTotalDamageDealtToChampions() > participant.getTotalDamageDealtToChampions()) {
                            embeds.add(buildEmbed(match, participant, support));
                        }
                        break;
                    }
                }
                break;
            }
        }

        return embeds;
    }

    private EmbedDto buildEmbed(MatchDto match, ParticipantDto participant, ParticipantDto support) {
        // Get queue name
        String queueName = leagueGameConstantsController.getQueue(match.getInfo().getQueueId()).getDescription();

        // Build description
        HashMap<String, Object> templateData = new HashMap<String, Object>();
        templateData.put("summoner", participant);
        templateData.put("match", match);
        templateData.put("participant", participant);
        templateData.put("support", participant);
        templateData.put("queueName", queueName);
        String description = templatingService.renderTemplate("templates/notifications/outdamaged_by_support.md.pebble", templateData);

        // Build embed
        EmbedDto embedDto = new EmbedDto();
        embedDto.setTitle(participant.getSummonerName() + " played Yuumi...");
        embedDto.setThumbnail(new ThumbnailDto(imageService.getChampionTileUrl(participant.getChampionName()).toString()));
        embedDto.setDescription(description);
        embedDto.setColor(ColorUtil.generateRandomColorFromString(participant.getSummonerName()));

        return embedDto;
    }
}
