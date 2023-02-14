package com.alistats.discorki.discord.command;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.alistats.discorki.discord.command.shared.AbstractCommand;
import com.alistats.discorki.discord.command.shared.Command;
import com.alistats.discorki.model.Server;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Component
public class Channel extends AbstractCommand implements Command {
    @Override
    public String getCommandName() {
        return "channel";
    }

    public void run(SlashCommandInteractionEvent event) {
        Long defaultChannelId = Optional.ofNullable(event.getOption("channel")).orElseThrow(() -> new RuntimeException("Channel cannot be empty.")).getAsLong();

        Server server = obtainServer(event.getGuild());
        server.setDefaultChannelId(defaultChannelId);
        serverRepo.save(server);
        
        reply(event, String.format("Default channel set to <#%s>.", defaultChannelId));
    }
}
