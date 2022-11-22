package com.alistats.discorki.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alistats.discorki.dto.discord.EmbedDto;
import com.alistats.discorki.dto.discord.FieldDto;
import com.alistats.discorki.dto.discord.FooterDto;
import com.alistats.discorki.dto.discord.ThumbnailDto;
import com.alistats.discorki.dto.discord.WebhookDto;
import com.alistats.discorki.dto.riot.match.MatchDto;
import com.alistats.discorki.dto.riot.match.ParticipantDto;
import com.alistats.discorki.model.Rank;
import com.alistats.discorki.util.ColorUtil;

/**
 * Builds a webhook dto. Contains predetermined values for the webhook.
 */
@Component
public class WebhookBuilder {
    @Autowired
    private ImageService imageService;

    public WebhookDto build(ArrayList<EmbedDto> embeds) throws Exception {
        WebhookDto webhookDto = new WebhookDto();
        webhookDto.setUsername("Discorki");
        try {
            webhookDto.setAvatar_url(imageService.getChampionTileUrl("Corki").toString());
        } catch (Exception e) {
            throw new Exception(e);
        }
        webhookDto.setEmbeds(embeds.toArray(new EmbedDto[embeds.size()]));
        return webhookDto;
    }

    public EmbedDto buildMatchEmbed(MatchDto matchDto, HashMap<ParticipantDto, Rank> summonerRanks) {
        // Divide into teams for easier access
        ArrayList<ParticipantDto> teamBlue = new ArrayList<ParticipantDto>();
        ArrayList<ParticipantDto> teamRed = new ArrayList<ParticipantDto>();

        for (ParticipantDto participant : matchDto.getInfo().getParticipants()) {
            if (participant.getTeamId() == 200) {
                teamBlue.add(participant);
            } else {
                teamRed.add(participant);
            }
        }

        // 1. Build the first field, containing the team compositions
        FieldDto teamComposition = new FieldDto();
        teamComposition.setName("Blue side");
        // Build blue side team composition
        StringBuilder teamCompositionValue = new StringBuilder();
        for (ParticipantDto participant : teamBlue) {
            teamCompositionValue.append(buildSummonerFieldLine(participant, participant.getTeamPosition()));
        }
        teamCompositionValue.append("\n\n**Red side**\n");

        // 2. Build red side team composition
        for (ParticipantDto participant : teamRed) {
            teamCompositionValue.append(buildSummonerFieldLine(participant, participant.getTeamPosition()));
        }
        teamComposition.setValue(teamCompositionValue.toString());
        teamComposition.setInline(true);

        // Build the second field, containing the damage of each summoner
        FieldDto damageComposition = new FieldDto();
        damageComposition.setName("Damage");
        // Build blue side damage composition
        StringBuilder damageCompositionValue = new StringBuilder();
        for (ParticipantDto participant : teamBlue) {
            damageCompositionValue
                    .append(NumberFormat.getIntegerInstance().format(participant.getTotalDamageDealtToChampions()))
                    .append("\n");
        }
        damageCompositionValue.append("\n\n\n");
        for (ParticipantDto participant : teamRed) {
            damageCompositionValue
                    .append(NumberFormat.getIntegerInstance().format(participant.getTotalDamageDealtToChampions()))
                    .append("\n");
        }
        damageComposition.setValue(damageCompositionValue.toString());
        damageComposition.setInline(true);

        // 3. Build the third field, containing the rank of each summoner
        FieldDto rankComposition = new FieldDto();
        rankComposition.setName("Rank");
        rankComposition.setInline(true);
        // Build blue side rank composition
        StringBuilder rankCompositionValue = new StringBuilder();

        // Build stream for ranks of participants
        for (ParticipantDto participant : teamBlue) {
            if (summonerRanks.get(participant) != null) {
                rankCompositionValue.append(buildRankFieldLine(summonerRanks.get(participant)));
            } else {
                rankCompositionValue.append("⚫ UNRANKED\n");
            }
        }
        rankCompositionValue.append("\n\n\n");
        for (ParticipantDto participant : teamRed) {
            if (summonerRanks.get(participant) != null) {
                rankCompositionValue.append(buildRankFieldLine(summonerRanks.get(participant)));
            } else {
                rankCompositionValue.append("⚫ UNRANKED\n");
            }
        }
        rankComposition.setValue(rankCompositionValue.toString());

        // Set the map thumbnail
        String mapThumbnail = imageService.getMapUrl(matchDto.getInfo().getMapId()).toString();

        // Set the footer
        FooterDto footerDto = new FooterDto();
        footerDto.setText("Discorki - A FreshCoders endeavour");

        // Build the embed
        EmbedDto embedDto = new EmbedDto();
        embedDto.setFields(
               new FieldDto[] { teamComposition, damageComposition, rankComposition });
        embedDto.setThumbnail(new ThumbnailDto(mapThumbnail));
        embedDto.setColor(ColorUtil.BLUE);
        embedDto.setFooter(footerDto);
        embedDto.setTitle("Game summary");

        return embedDto;
    }

    private String buildSummonerFieldLine(ParticipantDto participant, String teamPosition) {

        // encode url
        String opggUrl = "";
        try {
            String urlEncodedUsername = URLEncoder.encode(participant.getSummonerName(), "UTF-8");
            opggUrl = "https://euw.op.gg/summoner/userName=" + urlEncodedUsername;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        StringBuilder str = new StringBuilder();
        str.append(getRoleEmoji(teamPosition))
                .append(" [")
                .append(participant.getSummonerName())
                .append("](")
                .append(opggUrl)
                .append(") ")
                .append(participant.getChampionName())
                .append("\n");

        return str.toString();
    }

    private String buildRankFieldLine(Rank participantRank) {
        StringBuilder str = new StringBuilder();
        if (participantRank == null) {
            str.append("⚫ UNRANKED\n");
            return str.toString();
        }
        str.append(getRankTierEmoji(participantRank.getTier()))
                .append(" ")
                .append(participantRank.getTier())
                .append(" ")
                .append(participantRank.getDivision())
                .append(" - ")
                .append(participantRank.getLeaguePoints())
                .append("LP\n");

        return str.toString();
    }

    private String getRoleEmoji(String role) {
        switch (role) {
            case "TOP":
                return "🛡️";
            case "JUNGLE":
                return "🌳";
            case "MIDDLE":
                return "🪄";
            case "DUO_CARRY":
                return "🏹";
            case "DUO_SUPPORT":
                return "❤️‍🩹";
            default:
                return "";
        }
    }

    private String getRankTierEmoji(Rank.Tier tier) {
        switch (tier) {
            case CHALLENGER:
                return "🔴";
            case GRANDMASTER:
                return "⭕";
            case MASTER:
                return "🟣";
            case DIAMOND:
                return "🔵";
            case PLATINUM:
                return "🟢";
            case GOLD:
                return "🟡";
            case SILVER:
                return "⚪";
            case BRONZE:
                return "🟠";
            case IRON:
                return "🟤";
            default:
                return "⚫";
        }
    }
}
