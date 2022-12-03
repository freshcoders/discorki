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
public class TankNotification extends Notification implements ITeamPostGameNotification {
    
    public String name() {
        return "tank";
    }

    @Override
    public ArrayList<EmbedDto> check(MatchDto match, ArrayList<ParticipantDto> trackedParticipants) {

        ArrayList<EmbedDto> embeds = new ArrayList<EmbedDto>();

        for (ParticipantDto participant : trackedParticipants) {
            // Todo think about some way to make this more dynamic (compare to other's dmg?)
            if (participant.getDamageSelfMitigated() > 60000) {
                try {
                    embeds.add(buildEmbed(match, participant));
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        }

        return embeds;
    }

    private EmbedDto buildEmbed(MatchDto match, ParticipantDto participant) throws IOException {
        // Get queue name
        String queueName = leagueGameConstantsController.getQueue(match.getInfo().getQueueId()).getDescription();

        // Build description
        HashMap<String, Object> templateData = new HashMap<String, Object>();
        templateData.put("match", match);
        templateData.put("participant", participant);
        templateData.put("queueName", queueName);
        String description = templatingService.getNotificationTemplate(name(),
                templateData);

        // Build embed
        EmbedDto embedDto = new EmbedDto();
        embedDto.setTitle("SUPER SOAKER!");
        embedDto.setThumbnail(
                new ThumbnailDto(imageService.getChampionTileUrl(participant.getChampionName()).toString()));
        embedDto.setDescription(description);
        embedDto.setColor(ColorUtil.generateRandomColorFromString(participant.getSummonerName()));

        return embedDto;
    }
}
