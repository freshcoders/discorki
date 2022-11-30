package com.alistats.discorki.dto.riot.spectator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Getter
@Setter
public class ParticipantDto {
    private Long championId;
    private PerksDto perks;
    private Long profileIconId;
    private boolean bot;
    private Long teamId;
    private String summonerName;
    private String summonerId;
    private Long spell1Id;
    private Long spell2Id;
    private GameCustomizationObjectDto[] gameCustomizationObjects;


    @Override
    public String toString() {
        return "{" +
            " championId='" + getChampionId() + "'" +
            ", perks='" + getPerks() + "'" +
            ", profileIconId='" + getProfileIconId() + "'" +
            ", bot='" + isBot() + "'" +
            ", teamId='" + getTeamId() + "'" +
            ", summonerName='" + getSummonerName() + "'" +
            ", summonerId='" + getSummonerId() + "'" +
            ", spell1Id='" + getSpell1Id() + "'" +
            ", spell2Id='" + getSpell2Id() + "'" +
            ", gameCustomizationObjects='" + getGameCustomizationObjects() + "'" +
            "}";
    }

}
