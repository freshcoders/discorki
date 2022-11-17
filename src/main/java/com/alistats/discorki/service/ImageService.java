package com.alistats.discorki.service;

import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alistats.discorki.config.RiotConfigProperties;

@Service
public class ImageService {
    @Autowired
    private RiotConfigProperties config;

    public URL getChampionTileUrl(String championName) {
        StringBuilder str = new StringBuilder();
        str.append(config.getDataDragonUrl())
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
        str.append(config.getDataDragonUrl())
                .append("/cdn/img/champion/splash/")
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
}
