package com.alistats.discorki.dto.riot.league;
import com.alistats.discorki.model.Rank;
import com.alistats.discorki.model.Summoner;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Getter
@Setter
public class LeagueEntryDto {
    private String leagueId;
    private String summonerId;
    private String summonerName;
    private String queueType;
    private String tier;
    private String rank;
    private Integer leaguePoints;
    private Integer wins;
    private Integer losses;
    private Boolean veteran;
    private Boolean inactive;
    private Boolean freshBlood;
    private Boolean hotStreak;
    private MiniSeriesDto miniSeries;

    public Rank toRank(Summoner summoner) {
        Rank rank = new Rank();
        rank.setQueueType(this.queueType);
        rank.setTier(this.tier);
        rank.setRank(this.rank);
        rank.setLeaguePoints(this.leaguePoints);
        rank.setSummoner(summoner);
        return rank;
    }
}
