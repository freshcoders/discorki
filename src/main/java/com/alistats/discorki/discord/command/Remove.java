package com.alistats.discorki.discord.command;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

import com.alistats.discorki.discord.command.shared.AbstractCommand;
import com.alistats.discorki.discord.command.shared.Command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Component
public class Remove extends AbstractCommand implements Command{
    @Override
    public String getCommandName() {
        return "remove";
    }

    public void run(SlashCommandInteractionEvent event) {
        try {
            String userId = event.getOption("discord-username").getAsUser().getId();
            userRepo.deleteById(userId);
            event.getHook().sendMessage(String.format("Stopped tracking <@%s>", userId)).queue();
        } catch (EmptyResultDataAccessException e) {
            event.getHook().sendMessage("Something went wrong").queue();
        }
    }
}
