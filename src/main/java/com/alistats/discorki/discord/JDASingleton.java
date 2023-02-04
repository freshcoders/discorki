package com.alistats.discorki.discord;

import net.dv8tion.jda.api.JDA;

public class JDASingleton {
    private static JDA jda;

    public static JDA getJDA() {
        return jda;
    }

    public static void setJDA(JDA jda) {
        JDASingleton.jda = jda;
    }
}
