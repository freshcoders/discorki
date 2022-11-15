package com.alistats.discorki.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alistats.discorki.dto.SummonerDto;

@Service
public class LeagueApiController {
    @Value("${riot.api.key}")
    private String API_KEY;
    @Value("${riot.api.region}")
    private String API_REGION;
    @Value("${riot.api.url}")
    private String API_URL;

    RestTemplate restTemplate = new RestTemplate();
    
    public SummonerDto getSummoner(String summonerName) {
        SummonerDto summoner = restTemplate.getForObject("https://" + API_REGION + "." + API_URL + "/summoner/v4/summoners/by-name/" + summonerName + "?api_key=" + API_KEY, SummonerDto.class);

        return summoner;
    }
}
