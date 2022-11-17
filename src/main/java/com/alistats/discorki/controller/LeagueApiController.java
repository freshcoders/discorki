package com.alistats.discorki.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.alistats.discorki.config.RiotConfigProperties;
import com.alistats.discorki.dto.riot.league.LeagueEntryDto;
import com.alistats.discorki.dto.riot.match.MatchDto;
import com.alistats.discorki.dto.riot.spectator.CurrentGameInfoDto;
import com.alistats.discorki.dto.riot.summoner.SummonerDto;

@Service
public class LeagueApiController {
    @Autowired private RiotConfigProperties config;
    private RestTemplate restTemplate = new RestTemplate();

    public SummonerDto getSummoner(String summonerName) throws Exception {
        try {
            StringBuilder url = new StringBuilder();
            url .append("https://")
                .append(config.getPlatformRouting())
                .append(".")
                .append(config.getUrl())
                .append("/summoner/v4/summoners/by-name/")
                .append(summonerName)
                .append("?api_key=")
                .append(config.getKey());

            URI uri = URI.create(url.toString());

            return restTemplate.getForObject(uri, SummonerDto.class);
        } catch (final HttpClientErrorException e) {
            throw new Exception(e.getMessage());
        }
    }

    public CurrentGameInfoDto getCurrentGameInfo(String encryptedSummonerId) throws Exception {
        try {
            StringBuilder url = new StringBuilder();
            url .append("https://")
                .append(config.getPlatformRouting())
                .append(".")
                .append(config.getUrl())
                .append("/spectator/v4/active-games/by-summoner/")
                .append(encryptedSummonerId)
                .append("?api_key=")
                .append(config.getKey());

            URI uri = URI.create(url.toString());

            return restTemplate.getForObject(uri, CurrentGameInfoDto.class);
        } catch (final HttpClientErrorException e) {
            // First check if 404, then the game is just not found
            if (e.getStatusCode().value() == 404) {
                return null;
            }
            throw new Exception(e.getMessage());
        }
    }

    public String getMostRecentMatchId(String encryptedSummonerId) throws Exception {
        try {
            StringBuilder url = new StringBuilder();
            url .append("https://")
                .append(config.getRegionalRouting())
                .append(".")
                .append(config.getUrl())
                .append("/match/v5/matches/by-puuid/")
                .append(encryptedSummonerId)
                .append("/ids?start=0&count=1&api_key=")
                .append(config.getKey());

            URI uri = URI.create(url.toString());

            return restTemplate.getForObject(uri, String[].class)[0];
        } catch (final HttpClientErrorException e) {
            throw new Exception(e.getMessage());
        }
    }

    public MatchDto getMatch(String matchId) throws Exception {
        try {
            StringBuilder url = new StringBuilder();
            url .append("https://")
                .append(config.getRegionalRouting())
                .append(".")
                .append(config.getUrl())
                .append("/match/v5/matches/")
                .append(matchId)
                .append("?api_key=")
                .append(config.getKey());

            URI uri = URI.create(url.toString());

            return restTemplate.getForObject(uri, MatchDto.class);
        } catch (final HttpClientErrorException e) {
            throw new Exception(e.getMessage());
        }
    }

    public LeagueEntryDto[] getLeagueEntries(String encryptedSummonerId) throws Exception {
        try {
            StringBuilder url = new StringBuilder();
            url .append("https://")
                .append(config.getPlatformRouting())
                .append(".")
                .append(config.getUrl())
                .append("/league/v4/entries/by-summoner/")
                .append(encryptedSummonerId)
                .append("?api_key=")
                .append(config.getKey());

            URI uri = URI.create(url.toString());

            return restTemplate.getForObject(uri, LeagueEntryDto[].class);
        } catch (final HttpClientErrorException e) {
            throw new Exception(e.getMessage());
        }
    }
}
