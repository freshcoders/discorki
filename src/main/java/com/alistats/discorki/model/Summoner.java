package com.alistats.discorki.model;

import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "summoners")
public class Summoner {
    @Id
    private String accountId;
    private Integer profileIconId;
    private Long revisionDate;
    private String name;
    private String id;
    private String puuid;
    private Long summonerLevel;
    private Long currentGameId;
    @Column(columnDefinition = "boolean default false")
    private Boolean isTracked;
    @OneToMany(mappedBy = "summoner")
    private List<Rank> ranks;
    @ManyToMany(mappedBy = "trackedSummoners")
    private Set<Match> matches;

    public boolean isInGame() {
        return currentGameId != null;
    }

    @Override
    public String toString() {
        return "Summoner [accountId=" + accountId + ", profileIconId=" + profileIconId + ", revisionDate="
                + revisionDate + ", name=" + name + ", id=" + id + ", puuid=" + puuid + ", summonerLevel="
                + summonerLevel + ", currentGameId=" + currentGameId + ", isTracked=" + isTracked + "]";
    }
    
    
    
}