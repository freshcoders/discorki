package com.alistats.discorki.dto.riot.match;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Getter
@Setter
public class PerkStyleSelectionDto {
    private Integer perk;
    private Integer var1;
    private Integer var2;
    private Integer var3;
}
