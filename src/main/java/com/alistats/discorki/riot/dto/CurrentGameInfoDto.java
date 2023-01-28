package com.alistats.discorki.riot.dto;

import lombok.Data;

@Data
public class CurrentGameInfoDto {
    long gameId;
    String gameType;
    long gameStartTime;
    long mapId;
    long gameLength;
    String platformId;
    String gameMode;
    BannedChampionsDto[] bannedChampions;
    long gameQueueConfigId;
    ObserverDto observers;
    ParticipantDto[] participants;

    @Data
    public static class BannedChampionsDto {
        long championId;
        long teamId;
        int pickTurn;
    }

    @Data
    public static class ObserverDto {
        String encryptionKey;
    }

    @Data
    public static class ParticipantDto {
        long championId;
        PerksDto perks;
        long profileIconId;
        boolean bot;
        long teamId;
        String summonerName;
        String summonerId;
        long spell1Id;
        long spell2Id;
        GameCustomizationObjectDto[] gameCustomizationObjects;

        @Data
        public static class PerksDto {
            long perkStyle;
            long perkSubStyle;
            long[] perkIds;
        }

        @Data
        public static class GameCustomizationObjectDto {
            String category;
            String content;
        }
    }
}
