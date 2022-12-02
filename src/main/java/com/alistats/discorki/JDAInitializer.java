package com.alistats.discorki;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.alistats.discorki.config.DiscordConfigProperties;
import com.alistats.discorki.listener.SlashCommandListener;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;

@Component
public class JDAInitializer implements CommandLineRunner {
    @Autowired
    private DiscordConfigProperties discordConfigProperties;
    private final SlashCommandListener slashCommandListener;

    public JDAInitializer(SlashCommandListener messageListener) {
        this.slashCommandListener = messageListener;
    }

    @Override
    public void run(String... args) {
        JDA jda = JDABuilder.createDefault(discordConfigProperties.getToken())
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .build();

        jda.addEventListener(slashCommandListener);

        // add slash commands
        jda.updateCommands().addCommands(
            Commands.slash("add", "Add a user to Discorki")
                .addOption(OptionType.USER, "user", "The user to add", true)
                .addOption(OptionType.STRING, "summoner_name", "The in game name of the account", true),
            Commands.slash("remove", "Remove a user from Discorki")
                .addOption(OptionType.USER, "user", "The user to remove", true),
            Commands.slash("enable", "Enable a notification")
                .addOption(OptionType.STRING, "notification", "The notification to enable", true),
            Commands.slash("disable", "Disable a notification")
                .addOption(OptionType.STRING, "notification", "The notification to disable", true),
            Commands.slash("list_users", "List all users in Discorki"),
            Commands.slash("list_notifications", "List all notifications in Discorki"),
            Commands.slash("leaderboard", "Get ranked leaderboard for all summoners in this server")
        ).queue();
    }
}
