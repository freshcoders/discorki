package com.alistats.discorki.riot.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alistats.discorki.riot.dto.constants.ChampionDTO;
import com.alistats.discorki.riot.dto.constants.GameModeDto;
import com.alistats.discorki.riot.dto.constants.GameTypeDto;
import com.alistats.discorki.riot.dto.constants.MapDto;
import com.alistats.discorki.riot.dto.constants.QueueDto;
import com.alistats.discorki.riot.dto.constants.SeasonDto;

@Service
public class GameConstantsController {
    private RestTemplate restTemplate = new RestTemplate();

    private static final String BASE_URL_DOCS = "https://static.developer.riotgames.com/docs/lol";
    private static final String BASE_URL_DDRAGON = "http://ddragon.leagueoflegends.com/cdn/13.1.1/data/en_US";

    @Cacheable("gamemodes")
    public GameModeDto[] getGameModes() {
        return restTemplate.getForObject(BASE_URL_DOCS + "/gameModes.json", GameModeDto[].class);
    }

    @Cacheable("gametypes")
    public GameTypeDto[] getGameTypes() {
        return restTemplate.getForObject(BASE_URL_DOCS + "/gameTypes.json", GameTypeDto[].class);
    }

    @Cacheable("maps")
    public MapDto[] getMaps() {
        return restTemplate.getForObject(BASE_URL_DOCS + "/maps.json", MapDto[].class);
    }

    @Cacheable("queues")
    public QueueDto[] getQueues() {
        return restTemplate.getForObject(BASE_URL_DOCS + "/queues.json", QueueDto[].class);
    }

    @Cacheable("seasons")
    public SeasonDto[] getSeasons() {
        return restTemplate.getForObject(BASE_URL_DOCS + "/seasons.json", SeasonDto[].class);
    }

    @Cacheable("champions")
    public ChampionDTO getChampions() {
        return restTemplate.getForObject(BASE_URL_DDRAGON + "/champion.json", ChampionDTO.class);
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

    
    public Set<String> getChampionNames() {
        return getChampions().getData().keySet();
    }

    public String getChampionNameById(Integer id) {
        for (ChampionDTO.Champion champion : getChampions().getData().values()) {
            if (champion.getKey().equals(id)) {
                return champion.getName();
            }
        }
        return null;
    }
}
