package com.alistats.discorki.notification;

import java.io.IOException;
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
public class OutdamagedBySupportNotification extends Notification implements ITeamPostGameNotification {
    @Override
    public ArrayList<EmbedDto> check(MatchDto match, ArrayList<ParticipantDto> trackedParticipants) {

        ArrayList<EmbedDto> embeds = new ArrayList<EmbedDto>();

        // Since we COULD potentially have tracked players on opposite teams:
        // one being support on red, one being adc on blue, we SHOULD check
        // on team-basis. But this is extremely unlikely. For now, we can
        // just check on role and assume the tracked participants are on the same team.
        // After, we check team-equality, so we only have false negatives, no false
        // positives
        ParticipantDto support = null;
        ParticipantDto adc = null;

        for (ParticipantDto participant : trackedParticipants) {
            // Check if participant is adc
            if (participant.getTeamPosition().equals("BOTTOM")) {
                // Get participant playing support on that team
                adc = participant;
            }
            // Check if participant is support
            else if (participant.getTeamPosition().equals("SUPPORT")) {
                // Get participant playing adc on that team
                support = participant;
            }
        }

        // Support, or adc, or both are not tracked.
        if (support == null || adc == null || !support.getTeamId().equals(adc.getTeamId()))
            return embeds;

        // Check if support did more damage than adc
        if (support.getTotalDamageDealtToChampions() > adc.getTotalDamageDealtToChampions()) {
            try {
                embeds.add(buildEmbed(match, adc, support));
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        
        return embeds;
    }

    private EmbedDto buildEmbed(MatchDto match, ParticipantDto participant, ParticipantDto support) throws IOException {
        // Get queue name
        String queueName = leagueGameConstantsController.getQueue(match.getInfo().getQueueId()).getDescription();

        // Build description
        HashMap<String, Object> templateData = new HashMap<String, Object>();
        templateData.put("match", match);
        templateData.put("participant", participant);
        templateData.put("support", support);
        templateData.put("queueName", queueName);
        String description = templatingService.renderTemplate("templates/notifications/outdamaged_by_support.md.pebble",
                templateData);

        // Build embed
        EmbedDto embedDto = new EmbedDto();
        embedDto.setTitle(participant.getSummonerName() + " was outdamaged by his support!");
        embedDto.setThumbnail(
                new ThumbnailDto(imageService.getChampionTileUrl(participant.getChampionName()).toString()));
        embedDto.setDescription(description);
        embedDto.setColor(ColorUtil.generateRandomColorFromString(participant.getSummonerName()));

        return embedDto;
    }
}
