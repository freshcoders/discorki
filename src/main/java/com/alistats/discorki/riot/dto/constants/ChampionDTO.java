package com.alistats.discorki.riot.dto.constants;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChampionDTO {
    String type;
    String format;
    String version;
    Map<String, Champion> data;

    @Data
    public static class Champion {
        String version;
        String id;
        Integer key;
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
            Integer attack;
            Integer defense;
            Integer magic;
            Integer difficulty;
        }

        @Data
        public static class Image {
            String full;
            String sprite;
            String group;
            Integer x;
            Integer y;
            Integer w;
            Integer h;
        }

        @Data
        public static class Stats {
            Integer hp;
            Integer hpperlevel;
            Integer mp;
            Integer mpperlevel;
            Integer movespeed;
            double armor;
            double armorperlevel;
            double spellblock;
            double spellblockperlevel;
            Integer attackrange;
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
