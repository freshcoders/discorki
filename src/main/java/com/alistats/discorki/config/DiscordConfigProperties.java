package com.alistats.discorki.config;

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
@ConfigurationPropertiesScan
@ConfigurationProperties(prefix = "discord")
public class DiscordConfigProperties {
    @URL(message = "Webhook url is not set. Please refer to https://discord.com/developers/docs/intro for more information.")
    private String url;
    private String token;
}
