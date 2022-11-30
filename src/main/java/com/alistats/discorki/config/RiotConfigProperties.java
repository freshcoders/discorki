package com.alistats.discorki.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Component
@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "riot")
@ConfigurationPropertiesScan
public class RiotConfigProperties {
    @Pattern(regexp = "^RGAPI-[a-zA-Z0-9]{8}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{12}$", message = "Invalid API key. Must be in the format: RGAPI-XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX")
    private String key;
    @NotBlank(message = "Platform routing is not set, please refer to https://developer.riotgames.com/docs/lol for possible routes.")
    private String platformRouting;
    @NotBlank(message = "Regional routing is not set, please refer to https://developer.riotgames.com/docs/lol for possible routes.")
    private String regionalRouting;
    @NotBlank(message = "URL is not set. This is the base URL for the Riot API (api.riotgames.com/lol).")
    private String url;
    @URL(message = "Invalid URL. Use this: https://ddragon.leagueoflegends.com/cdn or check https://developer.riotgames.com/docs/lol for an updated URL.")
    private String dataDragonUrl;
    @NotBlank(message = "Version is not set. This is the version of the data dragon. Find an appropriate one (probably the latest) on https://ddragon.leagueoflegends.com/api/versions.json")
    private String dataDragonVersion;
    @URL(message = "Static data URL is not set. This is the base URL for the static data (https://static.developer.riotgames.com/docs/lol).")
    private String staticDataUrl;
}
