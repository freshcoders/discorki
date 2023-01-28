package com.alistats.discorki.discord.controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.alistats.discorki.config.CustomConfigProperties;
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
import com.alistats.discorki.riot.controller.GameConstantsController;
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
    @Autowired
    private GameConstantsController gameConstantsController;
    @Autowired
    private CustomConfigProperties config;

    private static final int ARAM_CHAMPS_PER_PLAYER = 3;

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        event.deferReply(false).queue();

        try {
            switch (event.getName()) {
                case "add" -> add(event);
                case "remove" -> remove(event);
                case "list" -> listUsers(event);
                case "leaderboard" -> leaderboard(event);
                case "unlink" -> unlink(event);
                case "channel" -> setDefaultChannel(event);
                case "debug" -> debug(event);
                case "aram" -> aram(event);
                case "generate-teams" -> generateTeams(event);
            }
        } catch (Exception e) {
            event.getHook().sendMessage("An error occurred.").queue();
            e.printStackTrace();
        }
    }

    private void debug(SlashCommandInteractionEvent event) {
        // Verify it's one of the allowed users
        if (!Arrays.asList(config.getDeveloperDiscordIds()).contains(event.getUser().getId())) {
            event.getHook().sendMessage("You are not allowed to use this command.").queue();
            return;
        }

        StringBuilder sb = new StringBuilder();

        // Get all database counts
        sb.append("**Database counts:**\r\n");
        sb.append("Guilds: ");
        sb.append(guildRepo.count());
        sb.append("\r\n");
        sb.append("Users: ");
        sb.append(userRepo.count());
        sb.append("\r\n");
        sb.append("Summoners: ");
        sb.append(summonerRepo.count());
        sb.append("\r\n");
        sb.append("Ranks: ");
        sb.append(rankRepo.count());
        sb.append("\r\n");
        sb.append("Matches: ");
        sb.append(matchRepo.count());
        sb.append("\r\n\r\n");

        // Get games in progress
        sb.append("**Games in progress:**\r\n");
        Optional<Set<Match>> matchesInProgressOpt = matchRepo.findByStatus(Status.IN_PROGRESS);
        if (matchesInProgressOpt.isPresent()) {
            Set<Match> matchesInProgress = matchesInProgressOpt.get();
            for (Match match : matchesInProgress) {
                sb.append(match.getTrackedSummoners().size());
                sb.append(" summoner(s) - ");
                sb.append(match.getId());
                sb.append(" (");
                sb.append(match.getGameQueueConfigId());
                sb.append(")\r\n");
            }
        } else {
            sb.append("*None*\r\n");
        }

        // Send dm to user
        event.getUser().openPrivateChannel().queue((channel) -> {
            channel.sendMessage(sb.toString()).queue();
        });
        event.getHook().sendMessage("Debug information sent to your DMs.").queue();
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
            event.getHook().sendMessage(
                    String.format("Summoner ***%s*** is already linked to <@%s>", summonerName, discordUser.getId()))
                    .queue();
            return;
        }

        // Check if summoner already exists
        Optional<Summoner> summonerOpt = summonerRepo.findByName(summonerName);
        if (summonerOpt.isPresent()) {
            Summoner summoner = summonerOpt.get();
            user.addSummoner(summoner);
            userRepo.save(user);
            event.getHook().sendMessage(String.format("Linked %s to <@%s>.", summoner.getName(), discordUser.getId()))
                    .queue();
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
            event.getHook().sendMessage(String.format("Linked %s to <@%s>.", summoner.getName(), discordUser.getId()))
                    .queue();
        } catch (Exception e) {
            if (e.getMessage().contains("404")) {
                event.getHook().sendMessage(String.format("Summoner ***%s*** not found.", summonerName)).queue();
            } else {
                throw e;
            }
        }

    }

    private void remove(SlashCommandInteractionEvent event) {
        try {
            String userId = event.getOption("discord-username").getAsUser().getId();
            userRepo.deleteById(userId);
            event.getHook().sendMessage(String.format("Stopped tracking <@%s>", userId)).queue();
        } catch (EmptyResultDataAccessException e) {
            event.getHook().sendMessage("Something went wrong").queue();
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
        event.getHook().sendMessage(String.format("Unlinked ***%s*** from <@%s>.", summoner.getName(), user.getId()))
                .queue();
    }

    private void setDefaultChannel(SlashCommandInteractionEvent event) {
        Guild guild = getGuild(event.getGuild());
        guild.setDefaultChannelId(event.getOption("channel").getAsLong());
        guildRepo.save(guild);
        event.getHook()
                .sendMessage(String.format("Default channel set to <#%s>.", event.getOption("channel").getAsLong()))
                .queue();
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

    private void aram(SlashCommandInteractionEvent event) {
        // Check if player size is in bounds
        int totalPlayerCount = event.getOption("player-count").getAsInt();
        if (totalPlayerCount < 1 || totalPlayerCount > 10) {
            event.getHook().sendMessage("Player count must be between 1 and 10").queue();
            return;
        }

        // Get all champions
        Set<String> championNames = gameConstantsController.getChampionNames();

        // Get total number of champions needed
        int totalChampionCount = totalPlayerCount * ARAM_CHAMPS_PER_PLAYER;

        // Get random champions
        List<String> randomChampions = championNames.stream()
                .collect(Collectors.collectingAndThen(Collectors.toList(), collected -> {
                    Collections.shuffle(collected);
                    return collected.stream();
                }))
                .limit(totalChampionCount)
                .collect(Collectors.toList());

        // Send the teams to the team captains
        net.dv8tion.jda.api.entities.User captain1 = event.getUser();
        net.dv8tion.jda.api.entities.User captain2 = event.getOption("other-captain").getAsUser();
        event.getHook()
                .sendMessage(
                        String.format("<@%s> and <@%s>, please check your DMs", captain1.getId(), captain2.getId()))
                .queue();

        // Get team sizes, if uneven, team red gets the extra player
        int team1Size = totalPlayerCount / 2;

        // Send team 1
        StringBuilder sb = new StringBuilder();
        sb.append("**Team 1:**").append("\r\n");
        for (int i = 0; i < team1Size; i++) {
            sb.append("Player ")
                    .append(i + 1)
                    .append(" - ")
                    .append(randomChampions.subList(i * ARAM_CHAMPS_PER_PLAYER, (i + 1) * ARAM_CHAMPS_PER_PLAYER))
                    .append("\r\n");
        }

        final String team1Message = sb.toString();
        event.getJDA().retrieveUserById(captain1.getId()).queue(user -> {
            user.openPrivateChannel().queue(privateChannel -> {
                privateChannel.sendMessage(team1Message).queue();
            });
        });

        // Send team 2
        sb = new StringBuilder();
        sb.append("**Team 2:**").append("\r\n");
        for (int i = team1Size; i < totalPlayerCount; i++) {
            sb.append("Player ")
                    .append(i + 1)
                    .append(" - ")
                    .append(randomChampions.subList(i * ARAM_CHAMPS_PER_PLAYER, (i + 1) * ARAM_CHAMPS_PER_PLAYER))
                    .append("\r\n");
        }
        final String team2Message = sb.toString();
        event.getJDA().retrieveUserById(captain2.getId()).queue(user -> {
            user.openPrivateChannel().queue(privateChannel -> {
                privateChannel.sendMessage(team2Message).queue();
            });
        });
    }

    private void generateTeams(SlashCommandInteractionEvent event) {
        // Get all players
        String playersConcatenated = event.getOption("players").getAsString();
        String[] players = playersConcatenated.split(",");

        // Shuffle players and trim whitespace
        List<String> shuffledPlayers = Arrays.asList(players)
                .stream()
                .map(String::trim)
                .collect(Collectors.toList());
        Collections.shuffle(shuffledPlayers);

        // Divide players into teams
        List<String> team1 = shuffledPlayers.subList(0, shuffledPlayers.size() / 2);
        List<String> team2 = shuffledPlayers.subList(shuffledPlayers.size() / 2, shuffledPlayers.size());

        // Send reply
        StringBuilder sb = new StringBuilder();
        sb.append("**Team 1:**\r\n");
        for (String player : team1) {
            sb.append("   - ").append(player).append("\r\n");
        }
        sb.append("\r\n**Team 2:**\r\n");
        for (String player : team2) {
            sb.append("   - ").append(player).append("\r\n");
        }
        event.getHook().sendMessage(sb.toString()).queue();
    }
}