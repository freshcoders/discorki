package com.alistats.discorki.discord.view;

import java.net.URL;
import java.util.List;

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

    public MessageEmbed build(List<String> teamRed, List<String> teamBlue, User captain1, User captain2) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("ARAM teams generated!");
        builder.setDescription(String.format("<@%s> and <@%s> , please check your DMs for the champion pools. Post them in lobby when the game starts!\r\n\r\n*Some rules we like to use: No exhaust. Trading allowed.*", captain1.getId(), captain2.getId()));
        builder.addField("Team Red", String.join("\r\n", teamRed), true);
        builder.addField("Team Blue", String.join("\r\n", teamBlue), true);
        builder.setFooter("Discorki - A FreshCoders endeavour");
        builder.setColor(5814783);
        builder.setThumbnail(getThummbnailUrl(captain1, captain2));

        return builder.build();
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
