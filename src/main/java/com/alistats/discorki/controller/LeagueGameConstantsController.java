package com.alistats.discorki.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alistats.discorki.config.RiotConfigProperties;
import com.alistats.discorki.dto.riot.constants.GameModeDto;
import com.alistats.discorki.dto.riot.constants.GameTypeDto;
import com.alistats.discorki.dto.riot.constants.MapDto;
import com.alistats.discorki.dto.riot.constants.QueueDto;
import com.alistats.discorki.dto.riot.constants.SeasonDto;

@Service
public class LeagueGameConstantsController {
    private RestTemplate restTemplate = new RestTemplate();
    @Autowired private RiotConfigProperties config;

    @Cacheable("gamemodes")
    public GameModeDto[] getGameModes() {
        return restTemplate.getForObject(config.getStaticDataUrl() + "/gameModes.json", GameModeDto[].class);
    }

    @Cacheable("gametypes")
    public GameTypeDto[] getGameTypes() {
        return restTemplate.getForObject(config.getStaticDataUrl() + "/gameTypes.json", GameTypeDto[].class);
    }

    @Cacheable("maps")
    public MapDto[] getMaps() {
        return restTemplate.getForObject(config.getStaticDataUrl() + "/maps.json", MapDto[].class);
    }

    @Cacheable("queues")
    public QueueDto[] getQueues() {
        return restTemplate.getForObject(config.getStaticDataUrl() + "/queues.json", QueueDto[].class);
    }

    @Cacheable("seasons")
    public SeasonDto[] getSeasons() {
        return restTemplate.getForObject(config.getStaticDataUrl() + "/seasons.json", SeasonDto[].class);
    }

    public QueueDto getQueue(Integer queueId) {
        List<QueueDto> queues = Arrays.asList(getQueues());
        for (QueueDto queue : queues) {
            if (queue.getQueueId().equals(queueId)) {
                return queue;
            }
        }
        return null;
    }
}
