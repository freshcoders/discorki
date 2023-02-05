package com.alistats.discorki.service;

import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alistats.discorki.config.CustomConfigProperties;
import com.alistats.discorki.model.Tier;
import com.alistats.discorki.riot.controller.GameConstantsController;
import com.alistats.discorki.util.StringUtil;

@Service
public class ImageService {
    @Autowired
    private CustomConfigProperties customConfig;
    @Autowired
    private GameConstantsController gameConstantsController;

    private static final String DATA_DRAGON_URL = "https://ddragon.leagueoflegends.com/cdn";
    private static final String DATA_DRAGON_VERSION = "12.22.1";

    // maybe the riot cdn can provide an image url or a slug for the champ
    public URL getChampionTileUrl(int championId) {
        String championName = gameConstantsController.getChampionIdByKey(championId);

        StringBuilder str = new StringBuilder();
        str.append(DATA_DRAGON_URL)
                .append("/")
                .append(DATA_DRAGON_VERSION)
                .append("/img/champion/")
                .append(championName)
                .append(".png");
        return resolveUrl(str.toString());
    }

    public URL getChampionSplashUrl(String championName) {
        championName = StringUtil.getCleanChampionName(championName);
        StringBuilder str = new StringBuilder();
        str.append(DATA_DRAGON_URL)
                .append("/img/champion/splash/")
                .append(championName)
                .append("_0.jpg");
        return resolveUrl(str.toString());
    }

    public URL getMapUrl(int mapId) {
        StringBuilder str = new StringBuilder();
        str.append(DATA_DRAGON_URL)
                .append("/")
                .append(DATA_DRAGON_VERSION)
                .append("/img/map/map")
                .append(mapId)
                .append(".png");

        return resolveUrl(str.toString());
    }

    public URL getRankEmblemUrl(Tier tier) {
        // tier should be a PascalCase string
        String tierStr = tier.toString().toLowerCase();

        StringBuilder str = new StringBuilder();
        str.append("http://")
                .append(customConfig.getHost())
                .append("/rank_emblems/")
                .append(tierStr)
                .append(".png");

        return resolveUrl(str.toString());
    }

    private URL resolveUrl(String url) {
        try {
            return new URL(url);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
