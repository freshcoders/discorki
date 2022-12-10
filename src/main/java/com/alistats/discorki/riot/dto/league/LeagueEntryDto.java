package com.alistats.discorki.riot.dto.league;
import com.alistats.discorki.model.Division;
import com.alistats.discorki.model.League;
import com.alistats.discorki.model.Rank;
import com.alistats.discorki.model.Tier;
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

    public Rank toRank() {
        Rank rank = new Rank();
        rank.setQueueType(this.queueType);
        League league = new League();
        league.setDivision(Division.valueOf(this.rank));
        league.setTier(Tier.valueOf(this.tier));
        rank.setLeague(league);
        rank.setLeaguePoints(this.leaguePoints);
        return rank;
    }
}
