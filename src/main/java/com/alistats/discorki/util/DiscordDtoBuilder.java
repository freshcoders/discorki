package com.alistats.discorki.util;

import java.util.ArrayList;

import com.alistats.discorki.dto.discord.EmbedDto;
import com.alistats.discorki.dto.discord.WebhookDto;

public class DiscordDtoBuilder {
    public static WebhookDto buildWebhook(ArrayList<EmbedDto> embeds) {
        WebhookDto webhookDto = new WebhookDto();
        webhookDto.setUsername("Discorki");
        webhookDto.setAvatar_url("https://www.mobafire.com/images/champion/square/corki.png");
        webhookDto.setEmbeds(embeds.toArray(new EmbedDto[embeds.size()]));
        return webhookDto;
    }
}
