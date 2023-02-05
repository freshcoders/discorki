package com.alistats.discorki.discord.command;

import java.io.IOException;

import org.springframework.stereotype.Component;

import com.alistats.discorki.discord.command.shared.AbstractCommand;
import com.alistats.discorki.discord.command.shared.Command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Component
public class Help extends AbstractCommand implements Command {
    @Override
    public String getCommandName() {
        return "help";
    }

    public void run(SlashCommandInteractionEvent event) {
        try {
            String helpText = templatingService.renderTemplate("templates/DiscordHelpCommand.pebble", null);
            event.getHook().sendMessage(helpText).queue();
        } catch (IOException e) {
            event.getHook().sendMessage("Something went wrong").queue();
        }
    }
}
