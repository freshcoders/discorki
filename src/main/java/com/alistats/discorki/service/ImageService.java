package com.alistats.discorki.service;

import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alistats.discorki.config.RiotConfigProperties;
import com.alistats.discorki.model.Rank;
import com.alistats.discorki.model.Rank.Division;
import com.alistats.discorki.model.Rank.Tier;

@Service
public class ImageService {
    @Autowired
    private RiotConfigProperties config;

    public URL getChampionTileUrl(String championName) {
        StringBuilder str = new StringBuilder();
        str .append(config.getDataDragonUrl())
            .append("/")
            .append(config.getDataDragonVersion())
            .append("/img/champion/")
            .append(championName)
            .append(".png");

        try {
            URL url = new URL(str.toString());
            return url;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public URL getChampionSplashUrl(String championName) {
        StringBuilder str = new StringBuilder();
        str .append(config.getDataDragonUrl())
            .append("/img/champion/splash/")
            .append(championName)
            .append("_0.jpg");

        try {
            URL url = new URL(str.toString());
            return url;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public URL getMapUrl(Integer mapId) {
        StringBuilder str = new StringBuilder();
        str .append(config.getDataDragonUrl())
            .append("/")
            .append(config.getDataDragonVersion())
            .append("/img/map/map")
            .append(mapId)
            .append(".png");

        try {
            URL url = new URL(str.toString());
            return url;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public URL getRankEmblemUrl(Division division, Tier tier) {
        // tier should be a PascalCase string
        String tierStr = tier.toString().substring(0, 1) + tier.toString().substring(1).toLowerCase();

        // division should be converted from roman numeral to arabic numeral
        Integer divisionNumber = Rank.divisionToInteger(division);

        // Use wikia for now. But we should selfhost
        StringBuilder str = new StringBuilder();
        str .append("https://static.wikia.nocookie.net/leagueoflegends/images/7/70/Season_2019_-_")
            .append(tierStr)
            .append("_")
            .append(divisionNumber)
            .append(".png");

        try {
            URL url = new URL(str.toString());
            return url;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }
}
