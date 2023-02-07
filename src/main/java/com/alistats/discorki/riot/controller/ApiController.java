package com.alistats.discorki.riot.controller;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.alistats.discorki.config.RestTemplateResponseErrorHandler;
import com.alistats.discorki.config.RiotConfigProperties;
import com.alistats.discorki.riot.dto.CurrentGameInfoDto;
import com.alistats.discorki.riot.dto.LeagueEntryDto;
import com.alistats.discorki.riot.dto.MatchDto;
import com.alistats.discorki.riot.dto.SummonerDto;
import com.google.common.util.concurrent.RateLimiter;

@Service
public class ApiController {
    private final RiotConfigProperties config;
    private final RestTemplate restTemplate;
    private final RateLimiter rateLimiter;

    private static final String BASE_URL = "api.riotgames.com/lol";
    private static final int TWO_MINUTES_IN_SECONDS = 120;

    // Since the checkInGame task is scheduled every 5 minutes at second 0
    // and the checkMatchFinished task is scheduled every minute at second 30,
    // we assume that the timeout wont interfere with new requests
    private static final int MAX_RATE_LIMIT_TIMEOUT = 30;

    public ApiController(RiotConfigProperties config, RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder
                .errorHandler(new RestTemplateResponseErrorHandler())
                .build();
        this.config = config;

        rateLimiter = RateLimiter.create(Double.parseDouble(config.getRateLimitPerTwoMinutes()) / TWO_MINUTES_IN_SECONDS);
    }

    private void rateLimit() {
        rateLimiter.tryAcquire(1, MAX_RATE_LIMIT_TIMEOUT, TimeUnit.SECONDS);
    }

    public SummonerDto getSummoner(String summonerName)
            throws HttpClientErrorException, HttpServerErrorException {
        rateLimit();
        StringBuilder url = new StringBuilder();
        url.append("https://")
                .append(config.getPlatformRouting())
                .append(".")
                .append(BASE_URL)
                .append("/summoner/v4/summoners/by-name/")
                .append(URLEncoder.encode(summonerName, StandardCharsets.UTF_8))
                .append("?api_key=")
                .append(config.getKey());

        URI uri = URI.create(url.toString());

        return restTemplate.getForObject(uri, SummonerDto.class);

    }

    public CurrentGameInfoDto getCurrentGameInfo(String encryptedSummonerId)
            throws HttpClientErrorException, HttpServerErrorException {
        rateLimit();
        StringBuilder url = new StringBuilder();
        url.append("https://")
                .append(config.getPlatformRouting())
                .append(".")
                .append(BASE_URL)
                .append("/spectator/v4/active-games/by-summoner/")
                .append(encryptedSummonerId)
                .append("?api_key=")
                .append(config.getKey());

        URI uri = URI.create(url.toString());

        return restTemplate.getForObject(uri, CurrentGameInfoDto.class);
    }

    public String getMostRecentMatchId(String encryptedSummonerId)
            throws HttpClientErrorException, HttpServerErrorException {
        rateLimit();
        StringBuilder url = new StringBuilder();
        url.append("https://")
                .append(config.getRegionalRouting())
                .append(".")
                .append(BASE_URL)
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
    public MatchDto getMatch(long matchId) throws HttpClientErrorException, HttpServerErrorException {
        rateLimit();
        StringBuilder url = new StringBuilder();
        url.append("https://")
                .append(config.getRegionalRouting())
                .append(".")
                .append(BASE_URL)
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
        rateLimit();
        StringBuilder url = new StringBuilder();
        url.append("https://")
                .append(config.getPlatformRouting())
                .append(".")
                .append(BASE_URL)
                .append("/league/v4/entries/by-summoner/")
                .append(encryptedSummonerId)
                .append("?api_key=")
                .append(config.getKey());

        URI uri = URI.create(url.toString());

        return restTemplate.getForObject(uri, LeagueEntryDto[].class);
    }
}
