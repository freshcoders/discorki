package com.alistats.discorki.util;

import java.util.Random;

import com.alistats.discorki.model.Rank.Tier;

public class ColorUtil {
    
    public static final int MAX_COLOR = 16777215;

    /**
     * Generates a random color in decimal format from a string.
     * Used for a specific color for en entity like user, server, champion, etc.
     * @param string the seed
     * @return decimal color
     */
    public static Integer generateRandomColorFromString(String string) {
        int sum = 0;
        for (int i = 0; i < string.length(); i++) {
            sum += (int) string.charAt(i);
        }
        Random random = new Random();
        random.setSeed(sum);

        return random.nextInt(MAX_COLOR);
    }

    // TODO: move to config file
    public static Integer getTierColor(Tier tier) {
        switch (tier) {
            case CHALLENGER:
                return 6668798;
            case GRANDMASTER:
                return 1711345;
            case MASTER:
                return 16726271;
            case DIAMOND:
                return 8137527;
            case PLATINUM:
                return 7656256;
            case GOLD:
                return 3393767;
            case SILVER:
                return 6118220;
            case BRONZE:
                return 2902683;
            case IRON:
                return 5592155;
            default:
                return null;
        }
    }
}
