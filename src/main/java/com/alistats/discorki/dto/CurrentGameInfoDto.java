package com.alistats.discorki.dto;

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
}
