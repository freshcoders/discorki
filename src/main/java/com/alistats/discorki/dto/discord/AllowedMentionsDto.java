package com.alistats.discorki.dto.discord;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Getter
@Setter
public class AllowedMentionsDto {
    private String[] parse;
    private String[] roles;
    private String[] users;
}
