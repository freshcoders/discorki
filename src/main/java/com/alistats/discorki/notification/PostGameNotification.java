package com.alistats.discorki.notification;

import org.springframework.beans.factory.annotation.Autowired;

import com.alistats.discorki.repository.SummonerRepo;
import com.alistats.discorki.service.GameConstantService;
import com.alistats.discorki.service.ImageService;
import com.alistats.discorki.service.TemplatingService;
public abstract class PostGameNotification {
    @Autowired protected SummonerRepo summonerRepo;
    @Autowired protected ImageService imageService;
    @Autowired protected GameConstantService gameConstantService; 
    @Autowired protected TemplatingService templatingService;
}
