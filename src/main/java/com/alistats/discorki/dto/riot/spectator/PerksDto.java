package com.alistats.discorki.dto.riot.spectator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Getter
@Setter
public class PerksDto {
    private long perkStyle;
    private long perkSubStyle;
    private long[] perkIds;
}
