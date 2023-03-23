package com.alistats.discorki.riot.dto;
import org.apache.commons.lang3.EnumUtils;

import com.alistats.discorki.model.Division;
import com.alistats.discorki.model.League;
import com.alistats.discorki.model.QueueType;
import com.alistats.discorki.model.Rank;
import com.alistats.discorki.model.Tier;

import lombok.Data;

@Data
public class LeagueEntryDto {
    String leagueId;
    String summonerId;
    String summonerName;
    String queueType;
    String tier;
    String rank;
    int leaguePoints;
    int wins;
    int losses;
    boolean veteran;
    boolean inactive;
    boolean freshBlood;
    boolean hotStreak;
    MiniSeriesDto miniSeries;

    public Rank toRank() throws Exception {
        Rank rank = new Rank();

        if (EnumUtils.isValidEnum(QueueType.class, this.queueType)) {
            rank.setQueueType(QueueType.valueOf(this.queueType));
        } else {
            throw new Exception("Invalid queue type: " + this.queueType);
        }
        League league = new League();
        league.setDivision(Division.valueOf(this.rank));
        league.setTier(Tier.valueOf(this.tier));
        rank.setLeague(league);
        rank.setLeaguePoints(this.leaguePoints);
        return rank;
    }

    @Data
    public static class MiniSeriesDto {
        int wins;
        int losses;
        int target;
        String progress;
    }
}
