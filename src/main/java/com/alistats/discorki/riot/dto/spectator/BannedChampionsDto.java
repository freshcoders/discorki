package com.alistats.discorki.riot.dto.spectator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Getter
@Setter
public class BannedChampionsDto {
    private long championId;
    private long teamId;
    private int pickTurn;
}
