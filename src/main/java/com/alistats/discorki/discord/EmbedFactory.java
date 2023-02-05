package com.alistats.discorki.discord;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
import com.alistats.discorki.model.Guild;
import com.alistats.discorki.model.Rank;
import com.alistats.discorki.model.Summoner;
import com.alistats.discorki.model.User;
import com.alistats.discorki.notification.result.GameStartNotificationResult;
import com.alistats.discorki.notification.result.PersonalPostGameNotificationResult;
import com.alistats.discorki.notification.result.TeamPostGameNotificationResult;
import com.alistats.discorki.riot.controller.GameConstantsController;
import com.alistats.discorki.riot.dto.MatchDto;
import com.alistats.discorki.riot.dto.MatchDto.InfoDto.ParticipantDto;
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
    final Logger logger = LoggerFactory.getLogger(EmbedFactory.class);
    @Autowired
    private CustomConfigProperties config;
    @Autowired
    private GameConstantsController gameConstantsController;

    final HashMap<String, String> roleEmojis = new HashMap<>() {
        {
            put("TOP", "🛡️");
            put("JUNGLE", "🌳");
            put("MIDDLE", "🔥");
            put("BOTTOM", "🏹");
            put("UTILITY", "❤️‍🩹");
        }
    };

    public Set<MessageEmbed> getEmbeds(TeamPostGameNotificationResult result) {
        EmbedBuilder builder = new EmbedBuilder();
        Set<MessageEmbed> embeds = new HashSet<>();

        // loop over hashmap
        for (Summoner summoner : result.getSubjects().keySet()) {
            ParticipantDto participant = result.getSubjects().get(summoner);
            String templatePath = String.format("templates/notifications/%s.pebble",
                    result.getNotification().getName());
            // build template
            HashMap<String, Object> templateArgs = new HashMap<>();
            templateArgs.put("participant", participant);
            templateArgs.put("match", result.getMatch());
            templateArgs.put("champion", gameConstantsController.getChampionNameByKey(participant.getChampionId()));
            templateArgs.put("extraArgs", result.getExtraArguments());
            try {
                String description = templatingService.renderTemplate(templatePath, templateArgs);
                builder.setDescription(description);
            } catch (Exception e) {
                logger.error("Error rendering template: {}", e.getMessage());
                continue;
            }

            builder.setTitle(result.getTitle());
            builder.setThumbnail(imageService.getChampionTileUrl(participant.getChampionId()).toString());

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
        if (result.getImage() != null) {
            builder.setImage(result.getImage().toString());
        }
        if (result.getThumbnail() != null) {
            builder.setThumbnail(result.getThumbnail().toString());
        }
        builder.setTitle(result.getTitle());

        return builder.build();
    }

    public Set<MessageEmbed> getEmbeds(GameStartNotificationResult result) {
        EmbedBuilder builder = new EmbedBuilder();
        Set<MessageEmbed> embeds = new HashSet<>();

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

    public MessageEmbed getMatchEmbed(Guild guild, MatchDto match, HashMap<ParticipantDto, Rank> particpantRanks)
            throws UnsupportedEncodingException {
        EmbedBuilder builder = new EmbedBuilder();
        List<List<ParticipantDto>> teams = match.getInfo().getTeamCategorizedParticipants();

        // Build fields
        builder.addField(buildTeamCompositionField(teams, guild));
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
        StringBuilder sb = new StringBuilder();
        sb.append("Match duration: ")
                .append(durationInMinutes)
                .append(" minutes.\n [Detailed game stats ↗️](")
                .append(String.format(config.getMatchLookupUrl(), match.getInfo().getGameId()))
                .append(")");
        builder.setDescription(sb.toString());

        return builder.build();
    }

    private MessageEmbed.Field buildTeamCompositionField(List<List<ParticipantDto>> teams, Guild guild) {
        // Build blue side team composition
        StringBuilder fieldValue = new StringBuilder();
        buildTeamComposition(fieldValue, teams.get(0), guild);
        fieldValue.append("\n\n**Red side**\n");
        buildTeamComposition(fieldValue, teams.get(1), guild);

        // Assemble the field

        return new MessageEmbed.Field("Blue side", fieldValue.toString(), true);
    }

    private void buildTeamComposition(StringBuilder fieldValue, List<ParticipantDto> team, Guild guild) {
        for (ParticipantDto participant : team) {
            boolean found = false;
            for (User user : guild.getUsers()) {
                for (Summoner summoner : user.getSummoners()) {
                    if (summoner.getId().equals(participant.getSummonerId())) {
                        fieldValue.append(
                                buildMentionedSummonerFieldLine(participant, participant.getTeamPosition(), user));
                        found = true;
                        break;
                    }
                }
                if (found)
                    break;
            }

            if (!found)
                fieldValue.append(buildSummonerFieldLine(participant, participant.getTeamPosition()));
        }
    }

    private String buildMentionedSummonerFieldLine(ParticipantDto participant, String teamPosition, User user) {
        // Build external link for summoner
        String urlEncodedUsername = URLEncoder.encode(participant.getSummonerName(), StandardCharsets.UTF_8);
        String summonerLookupUrl = String.format(config.getSummonerLookupUrl(), urlEncodedUsername);

        StringBuilder str = new StringBuilder();
        if (teamPosition != null && !teamPosition.equals("")) {
            str.append(roleEmojis.get(teamPosition));
        }

        // Get champion name
        String championName = gameConstantsController.getChampionNameByKey(participant.getChampionId());

        str.append(" <@")
                .append(user.getId())
                .append("> [↗️](")
                .append(summonerLookupUrl)
                .append(") ")
                .append(championName)
                .append("\n");

        return str.toString();
    }

    private String buildSummonerFieldLine(ParticipantDto participant, String teamPosition) {
        // Build external link for summoner
        String urlEncodedUsername = URLEncoder.encode(participant.getSummonerName(), StandardCharsets.UTF_8);
        String summonerLookupUrl = String.format(config.getSummonerLookupUrl(), urlEncodedUsername);

        StringBuilder str = new StringBuilder();
        if (teamPosition != null && !teamPosition.equals("")) {
            str.append(roleEmojis.get(teamPosition));
        }

        // Get champion name
        String championName = gameConstantsController.getChampionNameByKey(participant.getChampionId());

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

        return new MessageEmbed.Field("Damage", fieldValue.toString(), true);
    }

    private MessageEmbed.Field buildRankField(List<List<ParticipantDto>> teams,
            HashMap<ParticipantDto, Rank> summonerRanks) {
        StringBuilder fieldValue = new StringBuilder();

        // Build blue side ranks
        for (ParticipantDto participant : teams.get(0)) {
            if (summonerRanks.get(participant) != null) {
                fieldValue.append(buildRankFieldLine(summonerRanks.get(participant)));
            } else {
                fieldValue.append("🪵 Unranked\n");
            }
        }

        fieldValue.append("\n\n\n");

        // Build red side ranks
        for (ParticipantDto participant : teams.get(1)) {
            if (summonerRanks.get(participant) != null) {
                fieldValue.append(buildRankFieldLine(summonerRanks.get(participant)));
            } else {
                fieldValue.append("🪵 Unranked\n");
            }
        }

        // Assemble the field

        return new MessageEmbed.Field("Ranks", fieldValue.toString(), true);
    }

    public static String buildRankFieldLine(Rank rank) {
        StringBuilder sb = new StringBuilder();
        sb.append(rank.getLeague().getTier().getEmoji())
                .append(" ")
                .append(rank.getLeague().getTier().getFancyName())
                .append(" ");
        if (!rank.getLeague().getTier().isApex()) {
            sb.append(rank.getLeague().getDivision());
        } else {
            sb.append("(")
                    .append(rank.getLeaguePoints())
                    .append("LP)");
        }
        sb.append("\r\n");

        return sb.toString();
    }
}
