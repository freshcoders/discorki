package com.alistats.discorki.discord;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alistats.discorki.model.Guild;
import com.alistats.discorki.repository.GuildRepo;

import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Service
public class EventListener extends ListenerAdapter {
    @Autowired
    private GuildRepo guildRepo;

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        net.dv8tion.jda.api.entities.Guild discorGuild = event.getGuild();

        Optional<Guild> discordGuildOptional = guildRepo.findById(discorGuild.getId());

        // Check if guild is in database
        if (discordGuildOptional.isEmpty()) {
            // Add guild to database
            Guild guild = new Guild();
            guild.setId(discorGuild.getId());
            guild.setName(discorGuild.getName());

            guildRepo.save(guild);
        }

        discorGuild.getDefaultChannel().asTextChannel().sendMessage(
                "Hello, I am Discorki! I track achievements for League of Legends players. Add a player with /add!")
                .queue();
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        net.dv8tion.jda.api.entities.Guild discorGuild = event.getGuild();
        guildRepo.findById(discorGuild.getId()).orElseThrow(() -> new RuntimeException("Guild not found"))
                .setActive(false);
    }
}
