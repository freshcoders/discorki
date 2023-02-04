package com.alistats.discorki.discord.view;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alistats.discorki.service.ImageService;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

@Component
public class AramCommandView {
    @Autowired
    ImageService imageService;

    private final int ARAM_MAP_ID = 12;
    private final int EMBED_COLOR = 5814783;

    public MessageEmbed build(LinkedHashMap<String, Set<String>> teamBlue, LinkedHashMap<String, Set<String>> teamRed,
            User captain1, User captain2) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("ARAM teams generated!");
        builder.setDescription(String.format(
                "<@%s> and <@%s> , please check your DMs for the champion pools. Post them in lobby when the game starts!\r\n\r\n*Some rules we like to use: No exhaust. Trading allowed.*",
                captain1.getId(), captain2.getId()));
        builder.addField("Blue side", String.join("\r\n", teamBlue.keySet()), true);
        builder.addField("Red side", String.join("\r\n", teamRed.keySet()), true);
        builder.setColor(EMBED_COLOR);
        builder.setThumbnail(getThummbnailUrl(captain1, captain2));

        return builder.build();
    }

    public String buildTeamMessage(LinkedHashMap<String, Set<String>> team, boolean isBlueSide) {
        StringBuilder sb = new StringBuilder();
        sb.append("Your team is on **")
                .append(isBlueSide ? "Blue" : "Red")
                .append("** side. Here are your champion pools:\r\n\r\n");
        sb.append("```");
        for (Map.Entry<String, Set<String>> player : team.entrySet()) {
            sb.append(player.getKey())
                    .append(": ")
                    .append(String.join(", ", player.getValue()))
                    .append("\r\n");
        }
        sb.append("```\r\n*Paste the text above in the champion select lobby!*");

        return sb.toString();
    }

    private String getThummbnailUrl(User captain1, User captain2) {
        if (captain1.getAvatarUrl() == null || captain2.getAvatarUrl() == null) {
            return imageService.getMapUrl(ARAM_MAP_ID).toString();
        }

        try {
            URL url1 = new URL(captain1.getAvatarUrl());
            URL url2 = new URL(captain2.getAvatarUrl());
            URL mergedImage = imageService.mergeImagesDiagonally(url1, url2);

            return mergedImage.toString();
        } catch (Exception e) {
            return imageService.getMapUrl(ARAM_MAP_ID).toString();
        }
    }
}
