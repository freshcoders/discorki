package com.alistats.discorki.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
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

    public boolean isInGame() {
        return currentGameId != null;
    }
}