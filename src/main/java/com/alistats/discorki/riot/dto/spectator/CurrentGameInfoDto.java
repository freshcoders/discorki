package com.alistats.discorki.riot.dto.spectator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Getter
@Setter
public class CurrentGameInfoDto {
    private long gameId;
    private String gameType;
    private long gameStartTime;
    private long mapId;
    private long gameLength;
    private String platformId;
    private String gameMode;
    private BannedChampionsDto[] bannedChampions;
    private long gameQueueConfigId;
    private ObserverDto observers;
    private ParticipantDto[] participants;


    @Override
    public String toString() {
        return "{" +
            " gameId='" + getGameId() + "'" +
            ", gameType='" + getGameType() + "'" +
            ", gameStartTime='" + getGameStartTime() + "'" +
            ", mapId='" + getMapId() + "'" +
            ", gameLength='" + getGameLength() + "'" +
            ", platformId='" + getPlatformId() + "'" +
            ", gameMode='" + getGameMode() + "'" +
            ", bannedChampions='" + getBannedChampions() + "'" +
            ", gameQueueConfigId='" + getGameQueueConfigId() + "'" +
            ", observers='" + getObservers() + "'" +
            ", participants='" + getParticipants() + "'" +
            "}";
    }

}
