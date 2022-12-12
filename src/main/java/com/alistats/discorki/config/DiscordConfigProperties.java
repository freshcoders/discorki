package com.alistats.discorki.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Component
@Getter
@Setter
@Validated
@ConfigurationPropertiesScan
@ConfigurationProperties(prefix = "discord")
public class DiscordConfigProperties {
    @NotBlank
    private String token;
}
