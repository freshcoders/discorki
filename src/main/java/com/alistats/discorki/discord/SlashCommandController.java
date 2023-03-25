package com.alistats.discorki.discord;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alistats.discorki.discord.command.shared.Command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Service
public class SlashCommandController extends ListenerAdapter {
    final Logger LOG = LoggerFactory.getLogger(SlashCommandController.class);

    @Autowired
    private List<Command> commands;

    @Override
    @SuppressWarnings("null")
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        try {
            event.deferReply(false).queue();
        } catch (Exception e) {
            LOG.error("Error deferring reply, maybe we were too slow: {}", e.getMessage());
        }

        LOG.info("Received slash command: /{}", event.getName());
        
        commands.stream()
                .filter(command -> command.getCommandName().equals(event.getName()))
                .findFirst()
                .ifPresent(command -> {
                    try {
                        command.run(event);
                    } catch (Exception e) {
                        LOG.error("Error running command {}: {}", command.getCommandName(), e.getMessage());
                        event.getHook().sendMessage("❌ Oh no. An unexpected error!").queue();
                    }
                });
    }

}