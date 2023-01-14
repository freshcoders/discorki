package com.alistats.discorki.notification.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alistats.discorki.repository.SummonerRepo;
import com.alistats.discorki.riot.controller.ApiController;
import com.alistats.discorki.riot.controller.GameConstantsController;
import com.alistats.discorki.service.ImageService;
import com.alistats.discorki.service.TemplatingService;

import lombok.Setter;

public abstract class Notification {
    @Autowired protected ApiController leagueApiController;
    @Autowired protected SummonerRepo summonerRepo;
    @Setter
    @Autowired protected ImageService imageService;
    @Setter
    @Autowired protected TemplatingService templatingService;
    @Autowired protected GameConstantsController leagueGameConstantsController;

    protected Logger logger = LoggerFactory.getLogger(Notification.class);

    public String name() {
        throw new RuntimeException("Please implement name for " + this.getClass().getSimpleName());
    }

}
