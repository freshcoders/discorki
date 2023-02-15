package com.alistats.discorki.discord.command;

import java.util.Optional;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alistats.discorki.discord.command.shared.AbstractCommand;
import com.alistats.discorki.discord.command.shared.Command;
import com.alistats.discorki.model.Server;
import com.alistats.discorki.model.Player;
import com.alistats.discorki.model.Rank;
import com.alistats.discorki.model.Summoner;
import com.alistats.discorki.riot.dto.LeagueEntryDto;
import com.alistats.discorki.riot.dto.SummonerDto;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Component
public class Add extends AbstractCommand implements Command {

    @Override
    public String getCommandName() {
        return "add";
    }

    @Transactional(readOnly = false)
    public void run(SlashCommandInteractionEvent event) {
        // Get and validate options
        User jdaUser = Optional.ofNullable(event.getOption("discord-username"))
                .orElseThrow(() -> new RuntimeException("User cannot be empty.")).getAsUser();
        String summonerName = Optional.ofNullable(event.getOption("league-username"))
                .orElseThrow(() -> new RuntimeException("Summoner name cannot be empty.")).getAsString();

        // Check if user is not a bot
        if (jdaUser.isBot()) {
            event.getHook().sendMessage("Cannot link a bot.").queue();
            return;
        }

        // Get or create server and player
        Server server = obtainServer(event.getGuild());

        // Check if there already is a player with the same discord id and that guild
        Player player = playerRepo.findByDiscordIdAndServer(jdaUser.getId(), server)
                .orElseGet(() -> {
                    Player newPlayer = new Player(jdaUser, server);
                    playerRepo.save(newPlayer);
                    return newPlayer;
                });

        Hibernate.initialize(player.getSummoners());

        // Check if summoner was already linked
        if (player.hasSummonerByName(summonerName)) {
            String message = String.format("Summoner ***%s*** is already linked to <@%s>", summonerName,
                    jdaUser.getId());
            reply(event, message);
            return;
        }

        // Check if summoner already exists
        Summoner summoner = summonerRepo.findByName(summonerName).orElseGet(() -> {
            try {
                SummonerDto summonerDto = leagueApiController.getSummoner(summonerName);
                Summoner newSummoner = summonerDto.toSummoner();
                return summonerRepo.save(newSummoner);
            } catch (Exception e) {
                String message = e.getMessage().contains("404")
                        ? String.format("Summoner ***%s*** not found.", summonerName) : "An error occurred.";
                reply(event, message);
                return null;
            }
        });
        if (summoner == null) {
            return;
        }

        // Fetch rank
        try {
            LeagueEntryDto[] leagueEntryDtos = leagueApiController.getLeagueEntries(summoner.getId());

            // Save entries
            for (LeagueEntryDto leagueEntryDto : leagueEntryDtos) {
                Rank rank = leagueEntryDto.toRank();
                rank.setSummoner(summoner);
                rankRepo.save(rank);
            }

            // Add summoner to user
            player.addSummoner(summoner);
            playerRepo.save(player);
            String message = String.format("Linked %s to <@%s>.", summoner.getName(), jdaUser.getId());
            reply(event, message);
        } catch (Exception e) {
            String message;
            if (e.getMessage().contains("404")) {
                message = String.format("Summoner ***%s*** not found.", summonerName);
            } else {
                throw e;
            }
            reply(event, message);
        }
    }
}
