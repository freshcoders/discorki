package com.alistats.discorki.util;

import java.util.Random;

public class SummonerColorUtil {
    public static final int MAX_COLOR = 16777215;

    public static Integer createSummonerColor(String summonerName) {
        int sum = 0;
        for (int i = 0; i < summonerName.length(); i++) {
            sum += (int) summonerName.charAt(i);
        }
        Random random = new Random();
        random.setSeed(sum);

        return random.nextInt(MAX_COLOR);
    }   
}
