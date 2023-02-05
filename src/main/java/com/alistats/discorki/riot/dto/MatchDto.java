package com.alistats.discorki.riot.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class MatchDto {
    MetadataDto metadata;
    InfoDto info;

    @Data
    public static class MetadataDto {
        String dataVersion;
        String matchId;
        String[] participants;
    }

    @Data
    public static class InfoDto {
        long gameCreation;
        long gameDuration;
        long gameEndTimestamp;
        long gameId;
        String gameMode;
        String gameName;
        long gameStartTimestamp;
        String gameType;
        String gameVersion;
        int mapId;
        ParticipantDto[] participants;
        String platformId;
        int queueId;
        TeamDto[] teams;
        String tournamentCode;

        final int REMAKE_THRESHOLD_SECONDS_SR = 180;
        final int REMAKE_THRESHOLD_SECONDS_ARAM = 90;
        final int SUMMONERS_RIFT_MAP_ID = 1;

        public boolean isAborted() {
            if (this.getMapId() == SUMMONERS_RIFT_MAP_ID)
                return this.getGameDuration() < REMAKE_THRESHOLD_SECONDS_SR;
            else
                return this.getGameDuration() < REMAKE_THRESHOLD_SECONDS_ARAM;
        }

        public boolean isRanked() {
            return queueId == 420 || queueId == 440;
        }

        public List<List<ParticipantDto>> getTeamCategorizedParticipants() {
            List<List<ParticipantDto>> teams = new ArrayList<>();
            teams.add(new ArrayList<>());
            teams.add(new ArrayList<>());

            for (ParticipantDto participant : participants) {
                if (participant.getTeamId() == 100) {
                    teams.get(0).add(participant);
                } else {
                    teams.get(1).add(participant);
                }
            }

            return teams;
        }

        @Data
        public static class ParticipantDto {
            int assists;
            int baronKills;
            int bountyLevel;
            int champExperience;
            int champLevel;
            int championId;
            String championName;
            int championTransform;
            int consumablesPurchased;
            int damageDealtToBuildings;
            int damageDealtToObjectives;
            int damageDealtToTurrets;
            int damageSelfMitigated;
            int deaths;
            int detectorWardsPlaced;
            int doubleKills;
            int dragonKills;
            boolean firstBloodAssist;
            boolean firstBloodKill;
            boolean firstTowerAssist;
            boolean firstTowerKill;
            boolean gameEndedInEarlySurrender;
            boolean gameEndedInSurrender;
            int goldEarned;
            int goldSpent;
            String individualPosition;
            int inhibitorKills;
            int inhibitorTakedowns;
            int inhibitorsLost;
            int item0;
            int item1;
            int item2;
            int item3;
            int item4;
            int item5;
            int item6;
            int itemsPurchased;
            int killingSprees;
            int kills;
            String lane;
            int largestCriticalStrike;
            int largestKillingSpree;
            int largestMultiKill;
            int longestTimeSpentLiving;
            int magicDamageDealt;
            int magicDamageDealtToChampions;
            int magicDamageTaken;
            int neutralMinionsKilled;
            int nexusKills;
            int nexusTakedowns;
            int nexusLost;
            int objectivesStolen;
            int objectivesStolenAssists;
            int participantId;
            int pentaKills;
            PerksDto perks;
            int physicalDamageDealt;
            int physicalDamageDealtToChampions;
            int physicalDamageTaken;
            int profileIcon;
            String puuid;
            int quadraKills;
            String riotIdName;
            String riotIdTagline;
            String role;
            int sightWardsBoughtInGame;
            int spell1Casts;
            int spell2Casts;
            int spell3Casts;
            int spell4Casts;
            int summoner1Casts;
            int summoner1Id;
            int summoner2Casts;
            int summoner2Id;
            String summonerId;
            int summonerLevel;
            String summonerName;
            boolean teamEarlySurrendered;
            int teamId;
            String teamPosition;
            int timeCCingOthers;
            int timePlayed;
            int totalDamageDealt;
            int totalDamageDealtToChampions;
            int totalDamageShieldedOnTeammates;
            int totalDamageTaken;
            int totalHeal;
            int totalHealsOnTeammates;
            int totalMinionsKilled;
            int totalTimeCCDealt;
            int totalTimeSpentDead;
            int totalUnitsHealed;
            int tripleKills;
            int trueDamageDealt;
            int trueDamageDealtToChampions;
            int trueDamageTaken;
            int turretKills;
            int turretTakedowns;
            int turretsLost;
            int unrealKills;
            int visionScore;
            int visionWardsBoughtInGame;
            int wardsKilled;
            int wardsPlaced;
            boolean win;

            @Data
            public static class PerksDto {
                PerkStatsDto statPerks;
                PerkStyleDto[] styles;

                @Data
                public static class PerkStatsDto {
                    int defense;
                    int flex;
                    int offense;
                }

                @Data
                public static class PerkStyleDto {
                    String description;
                    PerkStyleSelectionDto[] selections;
                    int style;

                    @Data
                    public static class PerkStyleSelectionDto {
                        int perk;
                        int var1;
                        int var2;
                        int var3;
                    }
                }
            }
        }

        @Data
        public static class TeamDto {
            BanDto[] bans;
            ObjectivesDto objectives;
            int teamId;
            boolean win;

            @Data
            public static class BanDto {
                int championId;
                int pickTurn;
            }

            @Data
            public static class ObjectivesDto {
                ObjectiveDto baron;
                ObjectiveDto champion;
                ObjectiveDto dragon;
                ObjectiveDto inhibitor;
                ObjectiveDto riftHerald;
                ObjectiveDto tower;
        
                @Data
                public static class ObjectiveDto {
                    boolean first;
                    int kills;
                }
            }
        }
    }

}
