package com.alistats.discorki.config;

import javax.validation.constraints.NotBlank;

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
@ConfigurationProperties(prefix = "riot")
@ConfigurationPropertiesScan
// todo: add patterns for validation and messages
public class RiotConfigProperties {
    @NotBlank
    private String key;
    @NotBlank
    private String platformRouting;
    @NotBlank
    private String regionalRouting;
    @NotBlank
    private String url;
    @NotBlank
    private String dataDragonUrl;
    @NotBlank
    private String dataDragonVersion;
}
