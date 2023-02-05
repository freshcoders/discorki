package com.alistats.discorki.discord.command;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;

import com.alistats.discorki.discord.command.shared.AbstractCommand;
import com.alistats.discorki.discord.command.shared.Command;
import com.alistats.discorki.model.Guild;
import com.alistats.discorki.model.Summoner;
import com.alistats.discorki.model.User;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Component
public class List extends AbstractCommand implements Command{

    @Override
    public String getCommandName() {
        return "list";
    }

    public void run(SlashCommandInteractionEvent event) {
        Guild guild = getGuild(event.getGuild());
        StringBuilder sb = new StringBuilder();
        // for each user in guild
        Hibernate.initialize(guild.getUsers());
        sb.append("\r\n");
        for (User user : guild.getUsers()) {
            if (user.getSummoners().isEmpty()) {
                continue;
            }

            sb.append(user.getUsername())
                    .append("#")
                    .append(user.getDiscriminator())
                    .append("\r\n");
            // for each summoner in user
            for (Summoner summoner : user.getSummoners()) {
                sb.append("   - ")
                        .append(summoner.getName())
                        .append("\r\n");
            }
        }

        event.getHook().sendMessage(sb.toString()).queue();
    }
}
