package com.alistats.discorki.discord.command;

import org.springframework.stereotype.Component;

import com.alistats.discorki.discord.command.shared.AbstractCommand;
import com.alistats.discorki.discord.command.shared.Command;
import com.alistats.discorki.model.Guild;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Component
public class Channel extends AbstractCommand implements Command {
    @Override
    public String getCommandName() {
        return "channel";
    }

    public void run(SlashCommandInteractionEvent event) {
        Guild guild = getGuild(event.getGuild());
        guild.setDefaultChannelId(event.getOption("channel").getAsLong());
        guildRepo.save(guild);
        event.getHook()
                .sendMessage(String.format("Default channel set to <#%s>.", event.getOption("channel").getAsLong()))
                .queue();
    }
}
