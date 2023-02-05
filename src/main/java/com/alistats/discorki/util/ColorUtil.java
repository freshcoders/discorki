package com.alistats.discorki.util;

import java.util.Random;

public class ColorUtil {
    
    public static final int MAX_COLOR = 16777215;
    public static final int GREEN = 65280;
    public static final int RED = 16711680;
    public static final int BLUE = 255;

    /**
     * Generates a random color in decimal format from a string.
     * Used for a specific color for en entity like user, server, champion, etc.
     * @param string the seed
     * @return decimal color
     */
    public static int generateRandomColorFromString(String string) {
        int sum = 0;
        for (int i = 0; i < string.length(); i++) {
            sum += string.charAt(i);
        }
        Random random = new Random();
        random.setSeed(sum);

        return random.nextInt(MAX_COLOR);
    }
}
