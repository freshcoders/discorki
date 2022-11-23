package com.alistats.discorki.notification.common;

import org.springframework.beans.factory.annotation.Autowired;

import com.alistats.discorki.controller.LeagueApiController;
import com.alistats.discorki.controller.LeagueGameConstantsController;
import com.alistats.discorki.repository.SummonerRepo;
import com.alistats.discorki.service.ImageService;
import com.alistats.discorki.service.TemplatingService;

public abstract class Notification {
    @Autowired protected LeagueApiController leagueApiController;
    @Autowired protected SummonerRepo summonerRepo;
    @Autowired protected ImageService imageService;
    @Autowired protected TemplatingService templatingService;
    @Autowired protected LeagueGameConstantsController leagueGameConstantsController;
    
    public boolean enabled = true;
}
