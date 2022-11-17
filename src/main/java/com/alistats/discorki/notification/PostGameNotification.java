package com.alistats.discorki.notification;

import org.springframework.beans.factory.annotation.Autowired;

import com.alistats.discorki.repository.SummonerRepo;

public abstract class PostGameNotification {
    @Autowired SummonerRepo summonerRepo;
}
