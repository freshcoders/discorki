package com.alistats.discorki.util;

import java.util.Random;

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
}
