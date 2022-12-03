package com.alistats.discorki.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alistats.discorki.controller.DiscordController;
import com.alistats.discorki.controller.LeagueApiController;
import com.alistats.discorki.repository.MatchRepo;
import com.alistats.discorki.repository.SummonerRepo;
import com.alistats.discorki.view.DiscordWebhookView;

@Component
public abstract class Task {
    @Autowired 
    LeagueApiController leagueApiController;
    @Autowired
    DiscordController discordController;
    @Autowired
    SummonerRepo summonerRepo;
    @Autowired
    DiscordWebhookView webhookBuilder;
    @Autowired
    MatchRepo matchRepo;
    
    Logger logger = LoggerFactory.getLogger(Task.class);
}
