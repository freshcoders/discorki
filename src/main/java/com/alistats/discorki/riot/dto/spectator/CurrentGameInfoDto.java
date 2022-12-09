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
    private Long gameId;
    private String gameType;
    private Long gameStartTime;
    private Long mapId;
    private Long gameLength;
    private String platformId;
    private String gameMode;
    private BannedChampionsDto[] bannedChampions;
    private Long gameQueueConfigId;
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
