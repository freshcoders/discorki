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
@ConfigurationPropertiesScan
@ConfigurationProperties(prefix = "discord")
public class DiscordConfigProperties {
    @NotBlank private String url;
}
