package com.alistats.discorki.riot.dto.constants;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class ChampionDto {
    String type;
    String format;
    String version;
    Map<String, Champion> data;

    @Data
    public static class Champion {
        public enum Class {
            Assassin, Fighter, Mage, Marksman, Support, Tank
        }

        String version;
        String id;
        int key;
        String name;
        String title;
        String blurb;
        Info info;
        Image image;
        List<String> tags;
        String partype;
        Stats stats;

        @Data
        public static class Info {
            int attack;
            int defense;
            int magic;
            int difficulty;
        }

        @Data
        public static class Image {
            String full;
            String sprite;
            String group;
            int x;
            int y;
            int w;
            int h;
        }

        @Data
        public static class Stats {
            int hp;
            int hpperlevel;
            int mp;
            int mpperlevel;
            int movespeed;
            double armor;
            double armorperlevel;
            double spellblock;
            double spellblockperlevel;
            int attackrange;
            double hpregen;
            double hpregenperlevel;
            double mpregen;
            double mpregenperlevel;
            double crit;
            double critperlevel;
            double attackdamage;
            double attackdamageperlevel;
            double attackspeedperlevel;
            double attackspeed;
        }
    }

}
