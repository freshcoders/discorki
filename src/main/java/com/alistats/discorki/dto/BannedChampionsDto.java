package com.alistats.discorki.dto;

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
    private Integer pickTurn;
}
