package com.alistats.discorki.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alistats.discorki.discord.controller.WebhookController;
import com.alistats.discorki.riot.controller.ApiController;
import com.alistats.discorki.repository.MatchRepo;
import com.alistats.discorki.repository.SummonerRepo;
import com.alistats.discorki.discord.view.DiscordWebhookView;

@Component
public abstract class Task {
    @Autowired 
    ApiController leagueApiController;
    @Autowired
    WebhookController discordController;
    @Autowired
    SummonerRepo summonerRepo;
    @Autowired
    DiscordWebhookView webhookBuilder;
    @Autowired
    MatchRepo matchRepo;
    
    Logger logger = LoggerFactory.getLogger(Task.class);
}
