package com.alistats.discorki.config;

import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Component
@Getter
@Setter
@ConfigurationPropertiesScan
@ConfigurationProperties(prefix = "app")
public class CustomConfigProperties {
    @URL(message = "The url used by post game notifications for summoner lookup.")
    private String summonerLookupUrl;
    @URL(message = "The url used by post game notifications for match lookup.")
    private String matchLookupUrl;
    @URL(message = "The url used for ranked emblem images.")
    private String rankEmblemImageStorage;
}
