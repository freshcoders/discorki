package com.alistats.discorki.discord.controller;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.alistats.discorki.riot.controller.ApiController;
import com.alistats.discorki.riot.dto.league.LeagueEntryDto;
import com.alistats.discorki.riot.dto.summoner.SummonerDto;
import com.alistats.discorki.model.Guild;
import com.alistats.discorki.model.User;
import com.alistats.discorki.model.Rank;
import com.alistats.discorki.model.Summoner;
import com.alistats.discorki.repository.GuildRepo;
import com.alistats.discorki.repository.RankRepo;
import com.alistats.discorki.repository.SummonerRepo;
import com.alistats.discorki.repository.UserRepo;
import com.alistats.discorki.discord.view.DiscordLeaderboardView;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Service
public class SlashCommandController extends ListenerAdapter {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private GuildRepo guildRepo;
    @Autowired
    private RankRepo rankRepo;
    @Autowired
    private SummonerRepo summonerRepo;
    @Autowired
    private ApiController leagueApiController;
    @Autowired
    private DiscordLeaderboardView discordLeaderboardView;

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        try {
            switch (event.getName()) {
                case "add" -> add(event);
                case "remove" -> remove(event);
                case "list" -> listUsers(event);
                case "leaderboard" -> leaderboard(event);
            }
        } catch (Exception e) {
            event.reply("An error occured.").queue();
            e.printStackTrace();
        }
    }

    private void add(SlashCommandInteractionEvent event) throws Exception {
        // Get guild
        Guild guild = getOrCreateGuild(event.getGuild());
        User discordUser = new User();

        net.dv8tion.jda.api.entities.User user = event.getOption("user").getAsUser();
        String summonerName = event.getOption("summoner_name").getAsString();

        // Check if user is already in that guild
        if (guild.getUsers().stream().anyMatch(u -> u.getId().equals(user.getId()))) {
            // Get user
            discordUser = guild.getUsers().stream().filter(u -> u.getId().equals(user.getId())).findFirst()
                    .get();
        } else {
            // Create user
            discordUser.setId(user.getId());
            discordUser.setUsername(user.getName());
            discordUser.setDiscriminator(user.getDiscriminator());
            discordUser.setGuild(guild);
            discordUser = userRepo.save(discordUser);
        }

        // Check if user already has that summoner
        if (discordUser.getSummoners().stream().anyMatch(s -> s.getName().equals(summonerName))) {
            event.reply("That summoner is already added to that user.").queue();
        } else {
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
            discordUser.addSummoner(summoner);
            userRepo.save(discordUser);
            event.reply("Added summoner to user.").queue();
        }
    }

    private void remove(SlashCommandInteractionEvent event) {
        try {
            userRepo.deleteById(event.getOption("user").getAsUser().getId());
            event.reply("Stopped tracking that user.").queue();
        } catch (EmptyResultDataAccessException e) {
            event.reply("That user was not found in the database.").queue();
        }
    }

    private void listUsers(SlashCommandInteractionEvent event) {
        Guild guild = getOrCreateGuild(event.getGuild());
        StringBuilder sb = new StringBuilder();
        // for each user in guild
        Hibernate.initialize(guild.getUsers());
        for (User user : guild.getUsers()) {
            sb.append(user.getUsername())
                    .append("#")
                    .append(user.getDiscriminator() + "\r\n");
            // for each summoner in user
            for (Summoner summoner : user.getSummoners()) {
                sb.append("   - ")
                        .append(summoner.getName())
                        .append("\r\n");
            }
        }

        event.reply(sb.toString()).setEphemeral(false).queue();
    }

    private void leaderboard(SlashCommandInteractionEvent event) {
        Guild guild = getOrCreateGuild(event.getGuild());

        Set<Rank> ranks = new HashSet<>();

        // Get latest ranks for soloq and flexq of all summoners in guild
        Set<User> users = guild.getUsers();
        for (User user : users) {
            Set<Summoner> summoners = user.getSummoners();
            for (Summoner summoner : summoners) {
                Rank soloqRank = summoner.getCurrentSoloQueueRank();
                Rank flexqRank = summoner.getCurrentFlexQueueRank();
                if (soloqRank != null) {
                    ranks.add(soloqRank);
                }
                if (flexqRank != null) {
                    ranks.add(flexqRank);
                }
            }
        }

        event.reply(discordLeaderboardView.build(ranks)).setEphemeral(false).queue();
    }

    private Guild getOrCreateGuild(net.dv8tion.jda.api.entities.Guild guild) {
        Optional<Guild> discordGuildOptional = guildRepo.findById(guild.getId());

        // Check if guild is in database
        if (!discordGuildOptional.isPresent()) {
            // Add guild to database
            Guild newGuild = new Guild();
            newGuild.setId(guild.getId());
            newGuild.setName(guild.getName());

            guildRepo.save(newGuild);

            return newGuild;
        }

        return discordGuildOptional.get();
    }
}