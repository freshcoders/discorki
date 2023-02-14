package com.alistats.discorki.discord.command;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alistats.discorki.discord.command.shared.AbstractCommand;
import com.alistats.discorki.discord.command.shared.Command;
import com.alistats.discorki.model.Player;
import com.alistats.discorki.model.Server;
import com.alistats.discorki.model.Summoner;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Component
public class List extends AbstractCommand implements Command {

    @Override
    public String getCommandName() {
        return "list";
    }

    @Transactional(readOnly=true)
    public void run(SlashCommandInteractionEvent event) {
        Server server = obtainServer(event.getGuild());
        StringBuilder sb = new StringBuilder();
        // for each user in guild
        Hibernate.initialize(server.getPlayers());
        sb.append("\n");
        for (Player player : server.getPlayers()) {
            if (player.getSummoners().isEmpty()) {
                continue;
            }

            sb.append(player.getDiscordUsername())
                    .append("\n");
            // for each summoner in user
            for (Summoner summoner : player.getSummoners()) {
                sb.append("   - ")
                        .append(summoner.getName())
                        .append("\n");
            }
        }

        reply(event, sb.toString());
    }
}
