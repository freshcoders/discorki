package com.alistats.discorki.dto.riot.constants;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Getter
@Setter
public class QueueDto {
    private Integer queueId;
    private String map;
    private String description;
    private String notes;
}
