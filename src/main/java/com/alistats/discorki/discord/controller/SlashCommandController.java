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
                case "unlink" -> unlink(event);
            }
        } catch (Exception e) {
            event.reply("An error occured.").queue();
            e.printStackTrace();
        }
    }

    private void add(SlashCommandInteractionEvent event) throws Exception {
        // Get guild
        Guild guild = getOrCreateGuild(event.getGuild());
        net.dv8tion.jda.api.entities.User discordUser = event.getOption("user").getAsUser();
        User user = guild.getUserInGuildByUserId(discordUser.getId());
        String summonerName = event.getOption("summoner_name").getAsString();

        if (user == null) {
            // Create new user if not found
            user = new User(discordUser);
            user.setGuild(guild);
            user = userRepo.save(user);
        } else if (user.hasSummonerByName(summonerName)) {
            // Check if user already has that summoner
            event.reply("That summoner is already added to that user.").queue();
            return;
        }

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
            Rank rank = leagueEntryDto.toRank();
            rank.setSummoner(summoner);
            rankRepo.save(rank);
        }

        // Add summoner to user
        user.addSummoner(summoner);
        userRepo.save(user);
        event.reply("Added summoner to user.").queue();
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


    private void unlink(SlashCommandInteractionEvent event) {
        // unlink a summoner from a user
        User user = userRepo.findById(event.getOption("user").getAsUser().getId()).get();
        Summoner summoner = summonerRepo.findByName(event.getOption("summoner_name").getAsString()).get();
        user.removeSummoner(summoner);
        userRepo.save(user);
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