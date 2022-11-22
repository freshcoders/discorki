package com.alistats.discorki.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alistats.discorki.config.CustomConfigProperties;
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
    @Autowired
    private CustomConfigProperties config;

    HashMap<String, String> roleEmojis = new HashMap<String, String>() {
        {
            put("TOP", "🛡️");
            put("JUNGLE", "🌳");
            put("MIDDLE", "🔥");
            put("BOTTOM", "🏹");
            put("UTILITY", "❤️‍🩹");
        }
    };

    HashMap<Rank.Tier, String> tierEmojis = new HashMap<Rank.Tier, String>() {
        {
            put(Rank.Tier.CHALLENGER, "🔴");
            put(Rank.Tier.GRANDMASTER, "⭕");
            put(Rank.Tier.MASTER, "🟣");
            put(Rank.Tier.DIAMOND, "🔵");
            put(Rank.Tier.PLATINUM, "🟢");
            put(Rank.Tier.GOLD, "🟡");
            put(Rank.Tier.SILVER, "⚪");
            put(Rank.Tier.BRONZE, "🟠");
            put(Rank.Tier.IRON, "🟤");
        }
    };

    public WebhookDto build(ArrayList<EmbedDto> embeds) throws Exception {
        // prepare webhook values
        String avatarUrl = imageService.getChampionTileUrl("Corki").toString();
        String username = "Discorki";

        // build webhook
        WebhookDto webhookDto = new WebhookDto();
        webhookDto.setUsername(username);
        webhookDto.setAvatar_url(avatarUrl);
        webhookDto.setEmbeds(embeds.toArray(new EmbedDto[embeds.size()]));

        return webhookDto;
    }

    public EmbedDto buildMatchEmbed(MatchDto matchDto, HashMap<ParticipantDto, Rank> summonerRanks) {
        List<List<ParticipantDto>> teams = matchDto.getInfo().getTeamCategorizedParticipants();

        // Build fields
        FieldDto[] fields = new FieldDto[] {
                buildTeamCompositionField(teams),
                buildDamageField(teams),
                buildRankField(teams, summonerRanks)
        };

        // Build thumbnail with map icon
        String mapThumbnail = imageService.getMapUrl(matchDto.getInfo().getMapId()).toString();
        ThumbnailDto thumbnail = new ThumbnailDto(mapThumbnail);

        // Build the footer
        FooterDto footerDto = new FooterDto();
        footerDto.setText("Discorki - A FreshCoders endeavour");

        // Build the title
        boolean blueTeamWon = matchDto.getInfo().getTeams()[0].isWin();
        String title = blueTeamWon ? "Blue team won!" : "Red team won!";
        int color = blueTeamWon ? ColorUtil.BLUE : ColorUtil.RED;

        // Build the description
        int durationInMinutes = Math.round(matchDto.getInfo().getGameDuration() / 60);
        StringBuilder descriptionSb = new StringBuilder();
        descriptionSb.append("Match duration: ")
                .append(durationInMinutes)
                .append(" minutes.\n [Detailed game stats ↗️](")
                .append(String.format(config.getMatchLookupUrl(), matchDto.getInfo().getGameId()))
                .append(")");

        // Assemble the embed
        EmbedDto embedDto = new EmbedDto();
        embedDto.setTitle(title);
        embedDto.setDescription(descriptionSb.toString());
        embedDto.setThumbnail(thumbnail);
        embedDto.setColor(color);
        embedDto.setFields(fields);
        embedDto.setFooter(footerDto);

        return embedDto;
    }

    private FieldDto buildTeamCompositionField(List<List<ParticipantDto>> teams) {
        // Build blue side team composition
        StringBuilder fieldValue = new StringBuilder();
        for (ParticipantDto participant : teams.get(0)) {
            fieldValue.append(buildSummonerFieldLine(participant, participant.getTeamPosition()));
        }
        fieldValue.append("\n\n**Red side**\n");

        // Build red side team composition
        for (ParticipantDto participant : teams.get(1)) {
            fieldValue.append(buildSummonerFieldLine(participant, participant.getTeamPosition()));
        }

        // Assemble the field
        FieldDto field = new FieldDto();
        field.setName("Blue side");
        field.setValue(fieldValue.toString());
        field.setInline(true);

        return field;
    }

    private FieldDto buildDamageField(List<List<ParticipantDto>> teams) {
        StringBuilder fieldValue = new StringBuilder();

        // Build blue side damage
        for (ParticipantDto participant : teams.get(0)) {
            fieldValue
                    .append(NumberFormat.getIntegerInstance().format(participant.getTotalDamageDealtToChampions()))
                    .append("\n");
        }

        fieldValue.append("\n\n\n");

        // Build red side damage
        for (ParticipantDto participant : teams.get(1)) {
            fieldValue
                    .append(NumberFormat.getIntegerInstance().format(participant.getTotalDamageDealtToChampions()))
                    .append("\n");
        }

        // Assemble the field
        FieldDto field = new FieldDto();
        field.setName("Damage");
        field.setValue(fieldValue.toString());
        field.setInline(true);

        return field;
    }

    private FieldDto buildRankField(List<List<ParticipantDto>> teams, HashMap<ParticipantDto, Rank> summonerRanks) {
        StringBuilder fieldValue = new StringBuilder();

        // Build blue side ranks
        for (ParticipantDto participant : teams.get(0)) {
            if (summonerRanks.get(participant) != null) {
                fieldValue.append(buildRankFieldLine(summonerRanks.get(participant)));
            } else {
                fieldValue.append("🪵 UNRANKED\n");
            }
        }

        fieldValue.append("\n\n\n");

        // Build red side ranks
        for (ParticipantDto participant : teams.get(1)) {
            if (summonerRanks.get(participant) != null) {
                fieldValue.append(buildRankFieldLine(summonerRanks.get(participant)));
            } else {
                fieldValue.append("🪵 UNRANKED\n");
            }
        }

        FieldDto field = new FieldDto();
        field.setName("Rank");
        field.setInline(true);
        field.setValue(fieldValue.toString());

        return field;
    }

    private String buildSummonerFieldLine(ParticipantDto participant, String teamPosition) {
        // Build external link for summoner
        String summonerLookupUrl = "";
        try {
            String urlEncodedUsername = URLEncoder.encode(participant.getSummonerName(), "UTF-8");
            summonerLookupUrl = String.format(config.getSummonerLookupUrl(), urlEncodedUsername);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        StringBuilder str = new StringBuilder();
        if (teamPosition != null && !teamPosition.equals("")) {
            str.append(roleEmojis.get(teamPosition));
        }

        str.append(" [")
                .append(participant.getSummonerName())
                .append("](")
                .append(summonerLookupUrl)
                .append(") ")
                .append(participant.getChampionName())
                .append("\n");

        return str.toString();
    }

    private String buildRankFieldLine(Rank participantRank) {
        StringBuilder str = new StringBuilder();
        str.append(tierEmojis.get(participantRank.getTier()))
                .append(" ")
                .append(participantRank.getTier())
                .append(" ")
                .append(participantRank.getDivision())
                .append(" - ")
                .append(participantRank.getLeaguePoints())
                .append("LP\n");

        return str.toString();
    }
}
