package com.alistats.discorki.api.dto;

import lombok.Data;

@Data
public class InfoDto {
    long servers;
    long summoners;
    long matches;
    long matchesInProgress;
}
