package com.alistats.discorki.discord.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.alistats.discorki.discord.view.DiscordLeaderboardView;
import com.alistats.discorki.model.Guild;
import com.alistats.discorki.model.Match;
import com.alistats.discorki.model.Match.Status;
import com.alistats.discorki.model.Rank;
import com.alistats.discorki.model.Summoner;
import com.alistats.discorki.model.User;
import com.alistats.discorki.repository.GuildRepo;
import com.alistats.discorki.repository.MatchRepo;
import com.alistats.discorki.repository.RankRepo;
import com.alistats.discorki.repository.SummonerRepo;
import com.alistats.discorki.repository.UserRepo;
import com.alistats.discorki.riot.controller.ApiController;
import com.alistats.discorki.riot.dto.league.LeagueEntryDto;
import com.alistats.discorki.riot.dto.summoner.SummonerDto;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Service
public class SlashCommandController extends ListenerAdapter {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private RankRepo rankRepo;
    @Autowired
    private GuildRepo guildRepo;
    @Autowired
    private SummonerRepo summonerRepo;
    @Autowired
    private ApiController leagueApiController;
    @Autowired
    private DiscordLeaderboardView discordLeaderboardView;
    @Autowired
    MatchRepo matchRepo;

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        event.deferReply(false).queue();

        try {
            switch (event.getName()) {
                case "add" -> add(event);
                case "games" -> games(event);
                case "remove" -> remove(event);
                case "list" -> listUsers(event);
                case "leaderboard" -> leaderboard(event);
                case "unlink" -> unlink(event);
                case "channel" -> setDefaultChannel(event);
            }
        } catch (Exception e) {
            event.getHook().sendMessage("An error occured.").queue();
            e.printStackTrace();
        }
    }

    private void add(SlashCommandInteractionEvent event) throws Exception {
        // Get guild
        Guild guild = getGuild(event.getGuild());
        net.dv8tion.jda.api.entities.User discordUser = event.getOption("discord-username").getAsUser();
        // Check if user is not a bot
        if (discordUser.isBot()) {
            event.getHook().sendMessage("Cannot link a bot.").queue();
            return;
        }
        User user = guild.getUserInGuildByUserId(discordUser.getId());
        String summonerName = event.getOption("league-username").getAsString();

        if (user == null) {
            // Create new user if not found
            user = new User(discordUser);
            user.setGuild(guild);
            user = userRepo.save(user);
        } else if (user.hasSummonerByName(summonerName)) {
            event.getHook().sendMessage(String.format("Summoner ***%s*** is already linked to <@%s>", summonerName, discordUser.getId())).queue();
            return;
        }

        // Check if summoner already exists
        Optional<Summoner> summonerOpt = summonerRepo.findByName(summonerName);
        if (summonerOpt.isPresent()) {
            Summoner summoner = summonerOpt.get();
            user.addSummoner(summoner);
            userRepo.save(user);
            event.getHook().sendMessage(String.format("Linked %s to <@%s>.", summoner.getName(), discordUser.getId())).queue();
            return;
        }

        // Fetch summoner details
        try {
            SummonerDto summonerDto = leagueApiController.getSummoner(summonerName);
            Summoner summoner = summonerDto.toSummoner();
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
            event.getHook().sendMessage(String.format("Linked %s to <@%s>.", summoner.getName(), discordUser.getId())).queue();
        } catch (Exception e) {
            if (e.getMessage().contains("404")) {
                event.getHook().sendMessage(String.format("Summoner ***%s*** not found.", summonerName)).queue();
            } else {
                throw e;
            }
        }
        
        
    }

    private void games(SlashCommandInteractionEvent event) {
        try {
            ArrayList<Match> matchesInProgress = matchRepo.findByStatus(Status.IN_PROGRESS).orElseThrow();
            String trackingString;
            if (matchesInProgress.isEmpty()) {
                trackingString = "No games are being tracked.";
            } else {
                trackingString = "Games being tracked:\n";
                for (Match match : matchesInProgress) {
                    trackingString += String.format("%s\n", match.getId());
                }
            } 
            event.getHook().sendMessage(String.format("matches: ") + trackingString).queue();
        } catch (EmptyResultDataAccessException e) {
            event.getHook().sendMessage("That user was not found in the database.").queue();
        }
    }

    private void remove(SlashCommandInteractionEvent event) {
        try {
            String userId = event.getOption("discord-username").getAsUser().getId();
            userRepo.deleteById(userId);
            event.getHook().sendMessage(String.format("Stopped tracking <@%s>", userId)).queue();
        } catch (EmptyResultDataAccessException e) {
            event.getHook().sendMessage("That user was not found in the database.").queue();
        }
    }

    private void listUsers(SlashCommandInteractionEvent event) {
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
                    .append(user.getDiscriminator() + "\r\n");
            // for each summoner in user
            for (Summoner summoner : user.getSummoners()) {
                sb.append("   - ")
                        .append(summoner.getName())
                        .append("\r\n");
            }
        }

        event.getHook().sendMessage(sb.toString()).queue();
    }

    private void leaderboard(SlashCommandInteractionEvent event) {
        Guild guild = getGuild(event.getGuild());

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

        event.getHook().sendMessage(discordLeaderboardView.build(ranks)).queue();
    }


    private void unlink(SlashCommandInteractionEvent event) {
        // unlink a summoner from a user
        User user = userRepo.findById(event.getOption("discord-username").getAsUser().getId()).get();
        Summoner summoner = summonerRepo.findByName(event.getOption("league-username").getAsString()).get();
        user.removeSummonerById(summoner.getId());
        summoner.removeUserById(user.getId());
        userRepo.save(user);
        summonerRepo.save(summoner);
        event.getHook().sendMessage(String.format("Unlinked ***%s*** from <@%s>.", summoner.getName(), user.getId())).queue();
    }

    private void setDefaultChannel(SlashCommandInteractionEvent event) {
        Guild guild = getGuild(event.getGuild());
        guild.setDefaultChannelId(event.getOption("channel").getAsLong());
        guildRepo.save(guild);
        event.getHook().sendMessage(String.format("Default channel set to <#%s>.", event.getOption("channel").getAsLong())).queue();
    }

    private Guild getGuild(net.dv8tion.jda.api.entities.Guild guild) {
        return guildRepo.findById(guild.getId()).orElseGet(() -> {
            Guild newGuild = new Guild();
            newGuild.setId(guild.getId());
            newGuild.setName(guild.getName());
            newGuild.setDefaultChannelId(guild.getDefaultChannel().getIdLong());

            return guildRepo.save(newGuild);
        });
    }
}