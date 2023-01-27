package com.alistats.discorki.riot.dto.constants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MapDto {
    Integer mapId;
    String mapName;
    String notes;
}
