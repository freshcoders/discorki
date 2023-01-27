package com.alistats.discorki.discord.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alistats.discorki.config.CustomConfigProperties;
import com.alistats.discorki.model.Rank;
import com.alistats.discorki.model.Summoner;
import com.alistats.discorki.model.Tier;
import com.alistats.discorki.notification.result.GameStartNotificationResult;
import com.alistats.discorki.notification.result.PersonalPostGameNotificationResult;
import com.alistats.discorki.notification.result.TeamPostGameNotificationResult;
import com.alistats.discorki.riot.controller.GameConstantsController;
import com.alistats.discorki.riot.dto.match.MatchDto;
import com.alistats.discorki.riot.dto.match.ParticipantDto;
import com.alistats.discorki.service.ImageService;
import com.alistats.discorki.service.TemplatingService;
import com.alistats.discorki.util.ColorUtil;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

@Component
public class EmbedFactory {
    @Autowired
    TemplatingService templatingService;
    @Autowired
    ImageService imageService;
    Logger logger = LoggerFactory.getLogger(EmbedFactory.class);
    @Autowired
    private CustomConfigProperties config;
    @Autowired
    private GameConstantsController gameConstantsController;

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

    public Set<MessageEmbed> getEmbeds(TeamPostGameNotificationResult result) {
        EmbedBuilder builder = new EmbedBuilder();
        Set<MessageEmbed> embeds = new HashSet<MessageEmbed>();

        // loop over hashmap
        for (Summoner summoner : result.getSubjects().keySet()) {
            ParticipantDto participant = result.getSubjects().get(summoner);
            String templatePath = String.format("templates/notifications/%s.pebble",
                    result.getNotification().getName());
            // build template
            HashMap<String, Object> templateArgs = new HashMap<>();
            templateArgs.put("participant", participant);
            templateArgs.put("match", result.getMatch());
            templateArgs.put("extraArgs", result.getExtraArguments());
            try {
                String description = templatingService.renderTemplate(templatePath, templateArgs);
                builder.setDescription(description);
            } catch (Exception e) {
                logger.error("Error rendering template: {}", e.getMessage());
                continue;
            }

            builder.setTitle(result.getTitle());
            builder.setThumbnail(imageService.getChampionTileUrl(participant.getChampionName()).toString());

            embeds.add(builder.build());
        }

        return embeds;
    }

    public MessageEmbed getEmbed(PersonalPostGameNotificationResult result) {
        EmbedBuilder builder = new EmbedBuilder();
        String templatePath = String.format("templates/notifications/%s.pebble", result.getNotification().getName());
        // build template
        HashMap<String, Object> templateArgs = new HashMap<>();
        templateArgs.put("summoner", result.getSubject());
        templateArgs.put("match", result.getMatch());
        templateArgs.put("extraArgs", result.getExtraArguments());
        try {
            String description = templatingService.renderTemplate(templatePath, templateArgs);
            builder.setDescription(description);
        } catch (Exception e) {
            logger.error("Error rendering template: {}", e.getMessage());
        }

        builder.setTitle(result.getTitle());

        return builder.build();
    }

    public Set<MessageEmbed> getEmbeds(GameStartNotificationResult result) {
        EmbedBuilder builder = new EmbedBuilder();
        Set<MessageEmbed> embeds = new HashSet<MessageEmbed>();

        // for each subject in the result, create an embed
        for (Summoner summoner : result.getSubjects().keySet()) {
            String templatePath = String.format("templates/notifications/%s.pebble",
                    result.getNotification().getName());
            // build template
            HashMap<String, Object> templateArgs = new HashMap<>();
            templateArgs.put("summoner", summoner);
            templateArgs.put("participant", result.getSubjects().get(summoner));
            templateArgs.put("match", result.getMatch());
            templateArgs.put("extraArgs", result.getExtraArguments());
            try {
                String description = templatingService.renderTemplate(templatePath, templateArgs);
                builder.setDescription(description);
            } catch (Exception e) {
                logger.error("Error rendering template: {}", e.getMessage());
                continue;
            }

            builder.setTitle(result.getTitle());

            embeds.add(builder.build());
        }

        return embeds;
    }

    public MessageEmbed getMatchEmbed(MatchDto match, HashMap<ParticipantDto, Rank> particpantRanks) throws UnsupportedEncodingException {
        EmbedBuilder builder = new EmbedBuilder();
        List<List<ParticipantDto>> teams = match.getInfo().getTeamCategorizedParticipants();
        
        // Build fields
        builder.addField(buildTeamCompositionField(teams));
        builder.addField(buildDamageField(teams));
        builder.addField(buildRankField(teams, particpantRanks));

        // Build thumbnail with map icon
        String mapThumbnail = imageService.getMapUrl(match.getInfo().getMapId()).toString();
        builder.setThumbnail(mapThumbnail);

        // Build the footer
        builder.setFooter("Discorki - A FreshCoders endeavour");

        // Build the title
        boolean blueTeamWon = match.getInfo().getTeams()[0].isWin();
        String title = blueTeamWon ? "Blue team won!" : "Red team won!";
        int color = blueTeamWon ? ColorUtil.BLUE : ColorUtil.RED;
        builder.setColor(color);
        builder.setTitle(title);

        // Build the description
        int durationInMinutes = Math.round(match.getInfo().getGameDuration() / 60);
        StringBuilder descriptionSb = new StringBuilder();
        descriptionSb.append("Match duration: ")
                .append(durationInMinutes)
                .append(" minutes.\n [Detailed game stats ↗️](")
                .append(String.format(config.getMatchLookupUrl(), match.getInfo().getGameId()))
                .append(")");
        builder.setDescription(descriptionSb.toString());
        
        return builder.build();
    }

    private MessageEmbed.Field buildTeamCompositionField(List<List<ParticipantDto>> teams) throws UnsupportedEncodingException {
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
        MessageEmbed.Field field = new MessageEmbed.Field("Blue side", fieldValue.toString(), true);

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

        // Get champion name
        String championName = gameConstantsController.getChampionNameById(participant.getChampionId());

        str.append(" [")
                .append(participant.getSummonerName())
                .append("](")
                .append(summonerLookupUrl)
                .append(") ")
                .append(championName)
                .append("\n");

        return str.toString();
    }

    private MessageEmbed.Field buildDamageField(List<List<ParticipantDto>> teams) {
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
        MessageEmbed.Field field = new MessageEmbed.Field("Damage", fieldValue.toString(), true);

        return field;
    }

    private MessageEmbed.Field buildRankField(List<List<ParticipantDto>> teams, HashMap<ParticipantDto, Rank> summonerRanks) {
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

        // Assemble the field
        MessageEmbed.Field field = new MessageEmbed.Field("Ranks", fieldValue.toString(), true);

        return field;
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
