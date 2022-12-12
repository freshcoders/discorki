package com.alistats.discorki.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alistats.discorki.discord.controller.EmbedFactory;
import com.alistats.discorki.riot.controller.ApiController;
import com.alistats.discorki.repository.GuildRepo;
import com.alistats.discorki.repository.MatchRepo;
import com.alistats.discorki.repository.SummonerRepo;

@Component
public abstract class Task {
    @Autowired 
    ApiController leagueApiController;
    @Autowired
    SummonerRepo summonerRepo;
    @Autowired
    MatchRepo matchRepo;
    @Autowired
    GuildRepo guildRepo;
    @Autowired
    EmbedFactory embedFactory;
    
    Logger logger = LoggerFactory.getLogger(Task.class);
}
