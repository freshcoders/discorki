package com.alistats.discorki.discord.view;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alistats.discorki.config.CustomConfigProperties;
import com.alistats.discorki.discord.dto.EmbedDto;
import com.alistats.discorki.discord.dto.FieldDto;
import com.alistats.discorki.discord.dto.FooterDto;
import com.alistats.discorki.discord.dto.ThumbnailDto;
import com.alistats.discorki.discord.dto.WebhookDto;
import com.alistats.discorki.riot.dto.match.MatchDto;
import com.alistats.discorki.riot.dto.match.ParticipantDto;
import com.alistats.discorki.model.Rank;
import com.alistats.discorki.model.Tier;
import com.alistats.discorki.service.ImageService;
import com.alistats.discorki.util.ColorUtil;

/**
 * Builds a webhook dto. Contains predetermined values for the webhook.
 */
@Component
public class DiscordWebhookView {
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

    private static HashMap<Tier, String> tierEmojis = new HashMap<Tier, String>() {
        {
            put(Tier.CHALLENGER, "🔴");
            put(Tier.GRANDMASTER, "⭕");
            put(Tier.MASTER, "🟣");
            put(Tier.DIAMOND, "🔵");
            put(Tier.PLATINUM, "🟢");
            put(Tier.GOLD, "🟡");
            put(Tier.SILVER, "⚪");
            put(Tier.BRONZE, "🟠");
            put(Tier.IRON, "🟤");
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

    public EmbedDto buildMatchEmbed(MatchDto matchDto, HashMap<ParticipantDto, Rank> summonerRanks) throws UnsupportedEncodingException {
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

    private FieldDto buildTeamCompositionField(List<List<ParticipantDto>> teams) throws UnsupportedEncodingException {
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
        field.setName("Rank (SoloQ)");
        field.setInline(true);
        field.setValue(fieldValue.toString());

        return field;
    }

    private String buildSummonerFieldLine(ParticipantDto participant, String teamPosition) throws UnsupportedEncodingException {
        // Build external link for summoner
        String summonerLookupUrl = "";

        String urlEncodedUsername = URLEncoder.encode(participant.getSummonerName(), "UTF-8");
        summonerLookupUrl = String.format(config.getSummonerLookupUrl(), urlEncodedUsername);

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

    public static String buildRankFieldLine(Rank rank) {
        StringBuilder str = new StringBuilder();
        str.append(tierEmojis.get(rank.getLeague().getTier()))
                .append(" ")
                .append(rank.getLeague().getTier())
                .append(" ")
                .append(rank.getLeague().getDivision())
                .append(" - ")
                .append(rank.getLeaguePoints())
                .append("LP\n");

        return str.toString();
    }
}
