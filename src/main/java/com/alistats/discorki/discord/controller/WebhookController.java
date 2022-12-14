package com.alistats.discorki.discord.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.alistats.discorki.config.DiscordConfigProperties;
import com.alistats.discorki.discord.dto.WebhookDto;

@Service
public class WebhookController {
    @Autowired private DiscordConfigProperties config;
    Logger logger = LoggerFactory.getLogger(WebhookController.class);
    private RestTemplate restTemplate = new RestTemplate();

    public void send(WebhookDto webhookDto) throws HttpClientErrorException {
        restTemplate.postForEntity(config.getUrl(), webhookDto, String.class);
    }
}
