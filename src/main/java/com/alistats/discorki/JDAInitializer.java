package com.alistats.discorki;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.alistats.discorki.config.DiscordConfigProperties;
import com.alistats.discorki.discord.controller.SlashCommandController;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

@Component
public class JDAInitializer implements CommandLineRunner {
    @Autowired
    private DiscordConfigProperties discordConfigProperties;
    private final SlashCommandController slashCommandListener;

    public JDAInitializer(SlashCommandController slashCommandListener) {
        this.slashCommandListener = slashCommandListener;
    }

    @Override
    public void run(String... args) {
        JDA jda = JDABuilder.createDefault(discordConfigProperties.getToken())
                .build();

        jda.addEventListener(slashCommandListener);

        // add slash commands
        jda.updateCommands().addCommands(
                Commands.slash("add", "Add a user to Discorki")
                        .addOption(OptionType.USER, "user", "The user to add", true)
                        .addOption(OptionType.STRING, "summoner_name", "The in game name of the account", true)
                        .setGuildOnly(true),
                Commands.slash("remove", "Remove a user from Discorki")
                        .addOption(OptionType.USER, "user", "The user to remove", true)
                        .setGuildOnly(true),
                Commands.slash("list", "List all users in Discorki")
                        .setGuildOnly(true),
                Commands.slash("leaderboard", "Get ranked leaderboard for all summoners in this server")
                        .setGuildOnly(true),
                Commands.slash("unlink", "Unlink a summoner from a user")
                        .addOption(OptionType.USER, "discord username", "The user to unlink a summoner from", true)
                        .addOption(OptionType.STRING, "league username", "The in game name of the summoner to unlink",
                                true)
                        .setGuildOnly(true),
                Commands.slash("channel", "Set the default channel for Discorki to post in")
                        .addOption(OptionType.CHANNEL, "channel", "The channel to post in", true)
                        .setGuildOnly(true))
                .queue();

        JDASingleton.setJDA(jda);
    }
}
