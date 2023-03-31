package com.alistats.discorki.service;

import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alistats.discorki.config.CustomConfigProperties;
import com.alistats.discorki.config.RiotConfigProperties;
import com.alistats.discorki.model.Tier;
import com.alistats.discorki.riot.controller.GameConstantsController;
import com.alistats.discorki.util.StringUtil;

@Service
public class ImageService {
    @Autowired
    private CustomConfigProperties customConfig;
    @Autowired
    private RiotConfigProperties riotConfig;
    @Autowired
    private GameConstantsController gameConstantsController;

    private final String DATA_DRAGON_URL = "https://ddragon.leagueoflegends.com/cdn";
    private final String DATA_DRAGON_VERSION = riotConfig.getDataDragonVersion();

    // maybe the riot cdn can provide an image url or a slug for the champ
    public URL getChampionTileUrl(int championId) {
        String championName = gameConstantsController.getChampionIdByKey(championId);

        String str = DATA_DRAGON_URL +
                "/" +
                DATA_DRAGON_VERSION +
                "/img/champion/" +
                championName +
                ".png";
        return resolveUrl(str);
    }

    public URL getChampionSplashUrl(String championName) {
        championName = StringUtil.getCleanChampionName(championName);
        String str = DATA_DRAGON_URL +
                "/img/champion/splash/" +
                championName +
                "_0.jpg";
        return resolveUrl(str);
    }

    public URL getMapUrl(int mapId) {
        String str = DATA_DRAGON_URL +
                "/" +
                DATA_DRAGON_VERSION +
                "/img/map/map" +
                mapId +
                ".png";

        return resolveUrl(str);
    }

    public URL getRankEmblemUrl(Tier tier) {
        // tier should be a PascalCase string
        String tierStr = tier.toString().toLowerCase();

        String str = "https://" +
                customConfig.getHost() +
                "/rank_emblems/" +
                tierStr +
                ".png";

        return resolveUrl(str);
    }

    private URL resolveUrl(String url) {
        try {
            return new URL(url);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
