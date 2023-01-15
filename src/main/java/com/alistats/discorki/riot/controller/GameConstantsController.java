package com.alistats.discorki.riot.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alistats.discorki.riot.dto.constants.GameModeDto;
import com.alistats.discorki.riot.dto.constants.GameTypeDto;
import com.alistats.discorki.riot.dto.constants.MapDto;
import com.alistats.discorki.riot.dto.constants.QueueDto;
import com.alistats.discorki.riot.dto.constants.SeasonDto;

@Service
public class GameConstantsController {
    private RestTemplate restTemplate = new RestTemplate();

    private static final String BASE_URL = "https://static.developer.riotgames.com/docs/lol";

    @Cacheable("gamemodes")
    public GameModeDto[] getGameModes() {
        return restTemplate.getForObject(BASE_URL + "/gameModes.json", GameModeDto[].class);
    }

    @Cacheable("gametypes")
    public GameTypeDto[] getGameTypes() {
        return restTemplate.getForObject(BASE_URL + "/gameTypes.json", GameTypeDto[].class);
    }

    @Cacheable("maps")
    public MapDto[] getMaps() {
        return restTemplate.getForObject(BASE_URL + "/maps.json", MapDto[].class);
    }

    @Cacheable("queues")
    public QueueDto[] getQueues() {
        return restTemplate.getForObject(BASE_URL + "/queues.json", QueueDto[].class);
    }

    @Cacheable("seasons")
    public SeasonDto[] getSeasons() {
        return restTemplate.getForObject(BASE_URL + "/seasons.json", SeasonDto[].class);
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
