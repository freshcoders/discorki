package com.alistats.discorki;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.alistats.discorki.config.DiscordConfigProperties;
import com.alistats.discorki.listener.MessageListener;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

@Component
public class JDAInitializer implements CommandLineRunner {
    @Autowired
    private DiscordConfigProperties discordConfigProperties;
    private final MessageListener messageListener;

    public JDAInitializer(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    @Override
    public void run(String... args) {
        JDA jda = JDABuilder.createDefault(discordConfigProperties.getToken())
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .build();

        jda.addEventListener(new MessageListener());
    }
}
