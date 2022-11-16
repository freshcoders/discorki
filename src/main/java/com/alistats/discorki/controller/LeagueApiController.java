package com.alistats.discorki.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alistats.discorki.dto.CurrentGameInfoDto;
import com.alistats.discorki.dto.SummonerDto;

@Service
public class LeagueApiController {
    @Value("${riot.api.key}")
    private String API_KEY;
    @Value("${riot.api.region}")
    private String API_REGION;
    @Value("${riot.api.url}")
    private String API_URL;

    private RestTemplate restTemplate = new RestTemplate();
    
    public SummonerDto getSummoner(String summonerName) {
        SummonerDto summoner = restTemplate.getForObject("https://" + API_REGION + "." + API_URL + "/summoner/v4/summoners/by-name/" + summonerName + "?api_key=" + API_KEY, SummonerDto.class);

        return summoner;
    }

    public CurrentGameInfoDto getCurrentGameInfo(String encryptedSummonerId) {
        try {
            CurrentGameInfoDto currentGameInfo = restTemplate.getForObject("https://" + API_REGION + "." + API_URL + "/spectator/v4/active-games/by-summoner/" + encryptedSummonerId + "?api_key=" + API_KEY, CurrentGameInfoDto.class);
            return currentGameInfo;
        } catch (Exception e) {
            // Todo: handle exception
            System.out.println(e.getMessage());
        }

        return null;
    }
}
