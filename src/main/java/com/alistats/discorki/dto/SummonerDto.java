package com.alistats.discorki.dto;

import com.alistats.discorki.model.Summoner;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Getter
@Setter
public class SummonerDto {
    private String accountId;
    private Integer profileIconId;
    private Long revisionDate;
    private String name;
    private String id;
    private String puuid;
    private Long summonerLevel;

    public Summoner toSummoner() {
        Summoner summoner = new Summoner();
        summoner.setAccountId(accountId);
        summoner.setProfileIconId(profileIconId);
        summoner.setRevisionDate(revisionDate);
        summoner.setName(name);
        summoner.setId(id);
        summoner.setPuuid(puuid);
        summoner.setSummonerLevel(summonerLevel);
        return summoner;
    }
}
