package com.alistats.discorki.dto.riot.match;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Getter
@Setter
public class TeamDto {
    private BanDto[] bans;
    private ObjectivesDto objectives;
    private Integer teamId;
    private boolean win;
}
