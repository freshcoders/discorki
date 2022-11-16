package com.alistats.discorki.config;

import java.util.ArrayList;
import java.util.List;

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
@ConfigurationProperties(prefix = "app")
public class CustomConfigProperties {
    @NotBlank private List<String> usernames = new ArrayList<String>();
}
