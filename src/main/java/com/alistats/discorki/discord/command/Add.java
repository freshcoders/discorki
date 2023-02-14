package com.alistats.discorki.discord.command;

import java.util.Optional;

import org.springframework.stereotype.Component;

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

        // Get or create server
        Server server = obtainServer(event.getGuild());

        // Get or create player
        Player player = server.getUserInGuildByUserId(jdaUser.getId()).orElseGet(() -> {
            Player newPlayer = new Player(jdaUser);
            newPlayer.setServer(server);
            return playerRepo.save(newPlayer);
        });

        // Check if summoner was already linked
        if (player.hasSummonerByName(summonerName)) {
            String message = String.format("Summoner ***%s*** is already linked to <@%s>", summonerName,
                    jdaUser.getId());
            reply(event, message);
            return;
        }

        // Check if summoner already exists
        Optional<Summoner> summonerOpt = summonerRepo.findByName(summonerName);
        if (summonerOpt.isPresent()) {
            Summoner summoner = summonerOpt.get();
            player.addSummoner(summoner);
            playerRepo.save(player);
            String message = String.format("Linked %s to <@%s>.", summoner.getName(), jdaUser.getId());
            reply(event, message);
            return;
        }

        // Fetch summoner details
        try {
            SummonerDto summonerDto = leagueApiController.getSummoner(summonerName);
            Summoner summoner = summonerDto.toSummoner();
            summonerRepo.save(summoner);

            // Fetch rank
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
                message = "An error occurred.";
            }
            reply(event, message);
        }
    }
}
