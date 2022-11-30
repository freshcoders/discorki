package com.alistats.discorki.config;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotBlank;

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
    @NotBlank(message = "An array of all the summoners you want tracked.")
    private List<String> usernames = new ArrayList<String>();
    @URL(message = "The url used by post game notifications for summoner lookup.")
    private String summonerLookupUrl;
    @URL(message = "The url used by post game notifications for match lookup.")
    private String matchLookupUrl;
    @URL(message = "The url used for ranked emblem images.")
    private String rankEmblemImageStorage;
}
