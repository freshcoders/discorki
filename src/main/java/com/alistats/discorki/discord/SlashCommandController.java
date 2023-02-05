package com.alistats.discorki.discord;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alistats.discorki.discord.command.shared.Command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Service
public class SlashCommandController extends ListenerAdapter {
    @Autowired
    private List<Command> commands;

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        event.deferReply(false).queue();

        commands.stream()
                .filter(command -> command.getCommandName().equals(event.getName()))
                .findFirst()
                .ifPresent(command -> command.run(event));
    }

}