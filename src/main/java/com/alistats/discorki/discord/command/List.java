package com.alistats.discorki.discord.command;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;

import com.alistats.discorki.discord.command.shared.AbstractCommand;
import com.alistats.discorki.discord.command.shared.Command;
import com.alistats.discorki.model.Server;
import com.alistats.discorki.model.Summoner;
import com.alistats.discorki.model.Player;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Component
public class List extends AbstractCommand implements Command{

    @Override
    public String getCommandName() {
        return "list";
    }

    public void run(SlashCommandInteractionEvent event) {
        Server server = getGuild(event.getGuild());
        StringBuilder sb = new StringBuilder();
        // for each user in guild
        Hibernate.initialize(server.getPlayers());
        sb.append("\r\n");
        for (Player player : server.getPlayers()) {
            if (player.getSummoners().isEmpty()) {
                continue;
            }

            sb.append(player.getUsername())
                    .append("#")
                    .append(player.getDiscriminator())
                    .append("\r\n");
            // for each summoner in user
            for (Summoner summoner : player.getSummoners()) {
                sb.append("   - ")
                        .append(summoner.getName())
                        .append("\r\n");
            }
        }

        event.getHook().sendMessage(sb.toString()).queue();
    }
}
