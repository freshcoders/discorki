package com.alistats.discorki.riot.dto.match;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Getter
@Setter
public class PerkStyleDto {
    private String description;
    private PerkStyleSelectionDto[] selections;
    private Integer style;
}
