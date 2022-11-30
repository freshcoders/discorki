package com.alistats.discorki.dto.riot.spectator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Getter
@Setter
public class BannedChampionsDto {
    private Long championId;
    private Long teamId;
    private Integer pickTurn;
}
