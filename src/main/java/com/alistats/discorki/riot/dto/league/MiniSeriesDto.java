package com.alistats.discorki.riot.dto.league;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Getter
@Setter
public class MiniSeriesDto {
    private Integer wins;
    private Integer losses;
    private Integer target;
    private String progress;
}
