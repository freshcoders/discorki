package com.alistats.discorki.listener;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alistats.discorki.controller.LeagueApiController;
import com.alistats.discorki.dto.riot.league.LeagueEntryDto;
import com.alistats.discorki.dto.riot.summoner.SummonerDto;
import com.alistats.discorki.model.DiscordGuild;
import com.alistats.discorki.model.DiscordUser;
import com.alistats.discorki.model.Summoner;
import com.alistats.discorki.repository.DiscordGuildRepo;
import com.alistats.discorki.repository.RankRepo;
import com.alistats.discorki.repository.SummonerRepo;
import com.alistats.discorki.repository.UserRepo;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Service
public class SlashCommandListener extends ListenerAdapter
{
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private DiscordGuildRepo discordGuildRepo;
    @Autowired
    private RankRepo rankRepo;
    @Autowired
    private SummonerRepo summonerRepo;
    @Autowired
    private LeagueApiController leagueApiController;

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "add":
                add(event);
                break;
            case "remove":
                event.reply("This feature is still in development.").queue();
                break;
            case "enable":
                event.reply("This feature is still in development.").queue();
                break;
            case "disable":
                event.reply("This feature is still in development.").queue();
                break;
            case "list_users":
                event.reply("This feature is still in development.").queue();
                break;
            case "list_notifications":  
                event.reply("This feature is still in development.").queue();
                break;
        }
    }

    private void add(SlashCommandInteractionEvent event) {
        // Get guild
        DiscordGuild discordGuild = getOrCreateGuild(event.getGuild());
        DiscordUser discordUser = new DiscordUser();

        User user = event.getOption("user").getAsUser();
        String summonerName = event.getOption("summoner_name").getAsString();

        // Check if user is already in that guild
        if (discordGuild.getUsers() != null && discordGuild.getUsers().stream().anyMatch(u -> u.getId().equals(user.getId()))) {
            // Get user
            discordUser = discordGuild.getUsers().stream().filter(u -> u.getId().equals(user.getId())).findFirst().get();
        } else {
            // Create user
            discordUser.setId(user.getId());
            discordUser.setUsername(user.getName());
            discordUser.setDiscriminator(user.getDiscriminator());
            discordUser.setGuild(discordGuild);
            discordUser = userRepo.save(discordUser);
        }

        // Check if user already has that summoner
        if (discordUser.getSummoners() != null && discordUser.getSummoners().stream().anyMatch(s -> s.getName().equals(summonerName))) {
            event.reply("That summoner is already added to that user.").queue();
        } else {
            try {
                // Fetch summoner details
                SummonerDto summonerDto = leagueApiController.getSummoner(summonerName);
                Summoner summoner = summonerDto.toSummoner();
                summoner.setTracked(true);
                summonerRepo.save(summoner);

                // Fetch rank
                List<LeagueEntryDto> leagueEntryDtos = Arrays
                        .asList(leagueApiController.getLeagueEntries(summoner.getId()));

                // Save entries
                for (LeagueEntryDto leagueEntryDto : leagueEntryDtos) {
                    rankRepo.save(leagueEntryDto.toRank(summoner));
                }

                // Add summoner to user
                discordUser.getSummoners().add(summoner);
                userRepo.save(discordUser);
                event.reply("Added summoner to user.").queue();
            } catch (Exception e) {
                // TODO: handle exception
                event.reply("An error occured.").queue();
            }
        }
    }

    private DiscordGuild getOrCreateGuild(Guild guild) {
        Optional<DiscordGuild> discordGuildOptional = discordGuildRepo.findById(guild.getId());

        // Check if guild is in database
        if (!discordGuildOptional.isPresent()) {
            // Add guild to database
            DiscordGuild discordGuild = new DiscordGuild();
            discordGuild.setId(guild.getId());
            discordGuild.setName(guild.getName());
            
            discordGuildRepo.save(discordGuild);

            return discordGuild;
        }

        return discordGuildOptional.get();
    }
}