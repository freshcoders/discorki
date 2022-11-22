package com.alistats.discorki.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.alistats.discorki.config.DiscordConfigProperties;
import com.alistats.discorki.dto.discord.WebhookDto;

@Service
public class DiscordController {
    @Autowired private DiscordConfigProperties config;
    private RestTemplate restTemplate = new RestTemplate();

    public void sendWebhook(WebhookDto webhookDto) throws Exception {
        try {
            restTemplate.postForEntity(config.getUrl(), webhookDto, String.class);
        } catch (final HttpClientErrorException e) {
            throw new HttpClientErrorException(e.getStatusCode(), e.getStatusText());
        }
    }
}
