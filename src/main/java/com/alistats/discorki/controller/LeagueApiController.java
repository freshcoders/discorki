package com.alistats.discorki.controller;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.alistats.discorki.config.RiotConfigProperties;
import com.alistats.discorki.dto.riot.league.LeagueEntryDto;
import com.alistats.discorki.dto.riot.match.MatchDto;
import com.alistats.discorki.dto.riot.spectator.CurrentGameInfoDto;
import com.alistats.discorki.dto.riot.summoner.SummonerDto;

@Service
public class LeagueApiController {
    @Autowired
    private RiotConfigProperties config;
    private RestTemplate restTemplate;

    @Autowired
    public LeagueApiController(RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder
                .errorHandler(new RestTemplateResponseErrorHandler())
                .build();
    }

    public SummonerDto getSummoner(String summonerName)
            throws HttpClientErrorException, HttpServerErrorException, UnsupportedEncodingException {
        StringBuilder url = new StringBuilder();
        url.append("https://")
                .append(config.getPlatformRouting())
                .append(".")
                .append(config.getUrl())
                .append("/summoner/v4/summoners/by-name/")
                .append(URLEncoder.encode(summonerName, "UTF-8"))
                .append("?api_key=")
                .append(config.getKey());

        URI uri = URI.create(url.toString());

        return restTemplate.getForObject(uri, SummonerDto.class);

    }

    @Cacheable("currentGames")
    public CurrentGameInfoDto getCurrentGameInfo(String encryptedSummonerId)
            throws HttpClientErrorException, HttpServerErrorException {
        StringBuilder url = new StringBuilder();
        url.append("https://")
                .append(config.getPlatformRouting())
                .append(".")
                .append(config.getUrl())
                .append("/spectator/v4/active-games/by-summoner/")
                .append(encryptedSummonerId)
                .append("?api_key=")
                .append(config.getKey());

        URI uri = URI.create(url.toString());

        return restTemplate.getForObject(uri, CurrentGameInfoDto.class);
    }

    public String getMostRecentMatchId(String encryptedSummonerId)
            throws HttpClientErrorException, HttpServerErrorException {
        StringBuilder url = new StringBuilder();
        url.append("https://")
                .append(config.getRegionalRouting())
                .append(".")
                .append(config.getUrl())
                .append("/match/v5/matches/by-puuid/")
                .append(encryptedSummonerId)
                .append("/ids?start=0&count=1&api_key=")
                .append(config.getKey());

        URI uri = URI.create(url.toString());
        String[] matchIds = restTemplate.getForObject(uri, String[].class);
        if (matchIds != null) {
            return matchIds[0];
        }
        return null;
    }

    @Cacheable("matches")
    public MatchDto getMatch(Long matchId) throws HttpClientErrorException, HttpServerErrorException {
        StringBuilder url = new StringBuilder();
        url.append("https://")
                .append(config.getRegionalRouting())
                .append(".")
                .append(config.getUrl())
                .append("/match/v5/matches/")
                .append(config.getPlatformRouting().toUpperCase())
                .append("_")
                .append(matchId)
                .append("?api_key=")
                .append(config.getKey());

        URI uri = URI.create(url.toString());

        return restTemplate.getForObject(uri, MatchDto.class);
    }

    public LeagueEntryDto[] getLeagueEntries(String encryptedSummonerId)
            throws HttpClientErrorException, HttpServerErrorException {
        StringBuilder url = new StringBuilder();
        url.append("https://")
                .append(config.getPlatformRouting())
                .append(".")
                .append(config.getUrl())
                .append("/league/v4/entries/by-summoner/")
                .append(encryptedSummonerId)
                .append("?api_key=")
                .append(config.getKey());

        URI uri = URI.create(url.toString());

        return restTemplate.getForObject(uri, LeagueEntryDto[].class);
    }
}
