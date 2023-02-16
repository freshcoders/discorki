package com.alistats.discorki.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alistats.discorki.discord.EmbedFactory;
import com.alistats.discorki.repository.MatchRepo;
import com.alistats.discorki.repository.ServerRepo;
import com.alistats.discorki.repository.SummonerRepo;
import com.alistats.discorki.riot.controller.ApiController;

@Component
public abstract class Task {
    @Autowired 
    ApiController leagueApiController;
    @Autowired
    SummonerRepo summonerRepo;
    @Autowired
    MatchRepo matchRepo;
    @Autowired
    ServerRepo serverRepo;
    @Autowired
    EmbedFactory embedFactory;

    final Logger LOG = LoggerFactory.getLogger(Task.class);
}
