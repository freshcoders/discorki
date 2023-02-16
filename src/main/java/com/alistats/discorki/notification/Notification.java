package com.alistats.discorki.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alistats.discorki.repository.SummonerRepo;
import com.alistats.discorki.riot.controller.LeagueApiController;
import com.alistats.discorki.riot.controller.GameConstantsController;
import com.alistats.discorki.service.ImageService;

public abstract class Notification {
    @Autowired protected LeagueApiController leagueApiController;
    @Autowired protected SummonerRepo summonerRepo;
    @Autowired protected ImageService imageService;
    @Autowired protected GameConstantsController leagueGameConstantsController;

    final protected Logger LOG = LoggerFactory.getLogger(Notification.class);

    public abstract String getName();
    public abstract String getFancyName();
    public abstract String getDescription();
}
