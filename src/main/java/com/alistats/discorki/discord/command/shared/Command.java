package com.alistats.discorki.discord.command.shared;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface Command {
    String getCommandName();
    void run(SlashCommandInteractionEvent event) throws Exception;
}
