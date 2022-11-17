package com.alistats.discorki.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alistats.discorki.dto.discord.EmbedDto;
import com.alistats.discorki.dto.discord.WebhookDto;

/**
 * Builds a webhook dto. Contains predetermined values for the webhook.
 */
@Component
public class WebhookBuilder {
    @Autowired private ImageService imageService;

    public WebhookDto build(ArrayList<EmbedDto> embeds) throws Exception {
        WebhookDto webhookDto = new WebhookDto();
        webhookDto.setUsername("Discorki");
        try {
            webhookDto.setAvatar_url(imageService.getChampionTileUrl("Corki").toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        webhookDto.setEmbeds(embeds.toArray(new EmbedDto[embeds.size()]));
        return webhookDto;
    }
}
