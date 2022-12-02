package com.alistats.discorki;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.alistats.discorki.config.DiscordConfigProperties;

import net.dv8tion.jda.api.JDABuilder;

@Component
public class JDAInitializer implements CommandLineRunner {
    @Autowired private DiscordConfigProperties discordConfigProperties;

    @Override
    public void run(String... args) {
        JDABuilder builder = JDABuilder.createDefault(discordConfigProperties.getToken());
        builder.build();
    }
}
