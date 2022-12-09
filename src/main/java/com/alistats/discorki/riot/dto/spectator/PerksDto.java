package com.alistats.discorki.riot.dto.spectator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Getter
@Setter
public class PerksDto {
    private Long perkStyle;
    private Long perkSubStyle;
    private Long[] perkIds;
}
