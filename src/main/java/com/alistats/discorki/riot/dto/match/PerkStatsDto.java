package com.alistats.discorki.riot.dto.match;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Getter
@Setter
public class PerkStatsDto {
    private Integer defense;
    private Integer flex;
    private Integer offense;
}
