package com.alistats.discorki.service;

import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alistats.discorki.config.RiotConfigProperties;
import com.alistats.discorki.model.Rank.Division;
import com.alistats.discorki.model.Rank.Tier;
import com.alistats.discorki.util.StringUtil;

@Service
public class ImageService {
    @Autowired
    private RiotConfigProperties config;

    // maybe the riot cdn can provide an image url or a slug for the champ
    public URL getChampionTileUrl(String championName) {
        championName = StringUtil.getCleanChampionName(championName);

        StringBuilder str = new StringBuilder();
        str .append(config.getDataDragonUrl())
            .append("/")
            .append(config.getDataDragonVersion())
            .append("/img/champion/")
            .append(championName)
            .append(".png");
        return resolveUrl(str.toString());
    }

    public URL getChampionSplashUrl(String championName) {
        championName = StringUtil.getCleanChampionName(championName);
        StringBuilder str = new StringBuilder();
        str .append(config.getDataDragonUrl())
            .append("/img/champion/splash/")
            .append(championName)
            .append("_0.jpg");
        return resolveUrl(str.toString());
    }

    public URL getMapUrl(Integer mapId) {
        StringBuilder str = new StringBuilder();
        str .append(config.getDataDragonUrl())
            .append("/")
            .append(config.getDataDragonVersion())
            .append("/img/map/map")
            .append(mapId)
            .append(".png");

        return resolveUrl(str.toString());
    }

    public URL getRankEmblemUrl(Division division, Tier tier) {
        // tier should be a PascalCase string
        String tierStr = tier.toString().substring(0, 1) + tier.toString().substring(1).toLowerCase();

        // TODO: move to config
        StringBuilder str = new StringBuilder();
        str .append("https://jesdev.nl/discorki/rank_emblems/Season_2022_-_")
            .append(tierStr)
            .append(".png");

        return resolveUrl(str.toString());
    }

    private URL resolveUrl(String url) {
        try {
            URL resolvedUrl = new URL(url);
            return resolvedUrl;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }
}
