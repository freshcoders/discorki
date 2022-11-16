package com.alistats.discorki.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.alistats.discorki.dto.match.MatchDto;
import com.alistats.discorki.dto.spectator.CurrentGameInfoDto;
import com.alistats.discorki.dto.summoner.SummonerDto;

@Service
public class LeagueApiController {
    @Value("${riot.api.key}")
    private String API_KEY;
    @Value("${riot.api.region}")
    private String API_REGION;
    @Value("${riot.api.url}")
    private String API_URL;

    private RestTemplate restTemplate = new RestTemplate();
    
    public SummonerDto getSummoner(String summonerName) throws Exception {
        try {
            SummonerDto summoner = restTemplate.getForObject("https://" + API_REGION + "." + API_URL + "/summoner/v4/summoners/by-name/" + summonerName + "?api_key=" + API_KEY, SummonerDto.class);
            return summoner;
        } catch (final HttpClientErrorException e) {
            throw new Exception(e.getMessage());
        }
    }

    public CurrentGameInfoDto getCurrentGameInfo(String encryptedSummonerId) throws Exception {
        try {
            CurrentGameInfoDto currentGameInfo = restTemplate.getForObject("https://" + API_REGION + "." + API_URL + "/spectator/v4/active-games/by-summoner/" + encryptedSummonerId + "?api_key=" + API_KEY, CurrentGameInfoDto.class);
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
            return restTemplate.getForObject("https://europe." + API_URL + "/match/v5/matches/by-puuid/" + encryptedSummonerId + "/ids?start=0&count=1&api_key=" + API_KEY, String[].class)[0];
        } catch (final HttpClientErrorException e) {
            throw new Exception(e.getMessage());
        }
    }

    public MatchDto getMatch(String matchId) throws Exception {
        try {
            MatchDto match = restTemplate.getForObject("https://europe." + API_URL + "/match/v5/matches/" + matchId + "?api_key=" + API_KEY, MatchDto.class);

        return match;
        } catch (final HttpClientErrorException e) {
            throw new Exception(e.getMessage());
        }
    }
}
