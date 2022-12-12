package com.alistats.discorki.discord.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alistats.discorki.model.Summoner;
import com.alistats.discorki.notification.game_start.GameStartNotificationResult;
import com.alistats.discorki.notification.personal_post_game.PersonalPostGameNotificationResult;
import com.alistats.discorki.notification.team_post_game.TeamPostGameNotificationResult;
import com.alistats.discorki.riot.dto.match.ParticipantDto;
import com.alistats.discorki.service.ImageService;
import com.alistats.discorki.service.TemplatingService;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

@Component
public class EmbedFactory {
    @Autowired TemplatingService templatingService;
    @Autowired ImageService imageService;
    Logger logger = LoggerFactory.getLogger(EmbedFactory.class);

    EmbedBuilder builder = new EmbedBuilder();

    public Set<MessageEmbed> getEmbeds(TeamPostGameNotificationResult result) {
        Set<MessageEmbed> embeds = new HashSet<MessageEmbed>();

        // for each subject in the result, create an embed
        for(ParticipantDto participant : result.getSubjects()) {
            String templatePath = String.format("templates/notifications/%s.pebble", result.getNotification().getName());
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
        String templatePath = String.format("templates/notifications/%s.pebble", result.getNotification().getName());
        // build template
        HashMap<String, Object> templateArgs = new HashMap<>();
        templateArgs.put("summoner", result);
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
        Set<MessageEmbed> embeds = new HashSet<MessageEmbed>();

        // for each subject in the result, create an embed
        for(Summoner summoner : result.getSubjects()) {
            String templatePath = String.format("templates/notifications/%s.pebble", result.getNotification().getName());
            // build template
            HashMap<String, Object> templateArgs = new HashMap<>();
            templateArgs.put("summoner", summoner);
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
}
