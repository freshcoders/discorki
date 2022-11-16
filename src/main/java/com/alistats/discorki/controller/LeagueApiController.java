package com.alistats.discorki.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.alistats.discorki.config.RiotConfigProperties;
import com.alistats.discorki.dto.match.MatchDto;
import com.alistats.discorki.dto.spectator.CurrentGameInfoDto;
import com.alistats.discorki.dto.summoner.SummonerDto;

@Service
public class LeagueApiController {
    @Autowired private RiotConfigProperties config;
    private RestTemplate restTemplate = new RestTemplate();

    public SummonerDto getSummoner(String summonerName) throws Exception {
        try {
            SummonerDto summoner = restTemplate
                    .getForObject(
                            "https://" + config.getPlatformRouting() + "." + config.getUrl()
                                    + "/summoner/v4/summoners/by-name/" + summonerName + "?api_key=" + config.getKey(),
                            SummonerDto.class);
            return summoner;
        } catch (final HttpClientErrorException e) {
            throw new Exception(e.getMessage());
        }
    }

    public CurrentGameInfoDto getCurrentGameInfo(String encryptedSummonerId) throws Exception {
        try {
            CurrentGameInfoDto currentGameInfo = restTemplate.getForObject("https://" + config.getPlatformRouting()
                    + "." + config.getUrl() + "/spectator/v4/active-games/by-summoner/" + encryptedSummonerId
                    + "?api_key=" + config.getKey(), CurrentGameInfoDto.class);
            return currentGameInfo;
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
            return restTemplate.getForObject(
                    "https://" + config.getRegionalRouting() + "." + config.getUrl() + "/match/v5/matches/by-puuid/"
                            + encryptedSummonerId + "/ids?start=0&count=1&api_key=" + config.getKey(),
                    String[].class)[0];
        } catch (final HttpClientErrorException e) {
            throw new Exception(e.getMessage());
        }
    }

    public MatchDto getMatch(String matchId) throws Exception {
        try {
            MatchDto match = restTemplate.getForObject("https://" + config.getRegionalRouting() + "." + config.getUrl()
                    + "/match/v5/matches/" + matchId + "?api_key=" + config.getKey(), MatchDto.class);

            return match;
        } catch (final HttpClientErrorException e) {
            throw new Exception(e.getMessage());
        }
    }
}
