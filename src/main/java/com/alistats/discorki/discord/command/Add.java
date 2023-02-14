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
        // Get guild
        Server server = getGuild(event.getGuild());
        User jdaUser = event.getOption("discord-username").getAsUser();
        // Check if user is not a bot
        if (jdaUser.isBot()) {
            event.getHook().sendMessage("Cannot link a bot.").queue();
            return;
        }

        String summonerName = event.getOption("league-username").getAsString();
        Optional<Player> userOpt = server.getUserInGuildByUserId(jdaUser.getId());

        if (userOpt.isEmpty()) {
            // Create new user if not found
            Player newPlayer = new Player(jdaUser);
            newPlayer.setServer(server);
            newPlayer = userRepo.save(newPlayer);
            userOpt = Optional.of(newPlayer);
        } else if (userOpt.get().hasSummonerByName(summonerName)) {
            event.getHook().sendMessage(
                    String.format("Summoner ***%s*** is already linked to <@%s>", summonerName, jdaUser.getId()))
                    .queue();
            return;
        }

        Player player = userOpt.get();

        // Check if summoner already exists
        Optional<Summoner> summonerOpt = summonerRepo.findByName(summonerName);
        if (summonerOpt.isPresent()) {
            Summoner summoner = summonerOpt.get();
            player.addSummoner(summoner);
            userRepo.save(player);
            event.getHook().sendMessage(String.format("Linked %s to <@%s>.", summoner.getName(), jdaUser.getId()))
                    .queue();
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
            userRepo.save(player);
            event.getHook().sendMessage(String.format("Linked %s to <@%s>.", summoner.getName(), jdaUser.getId()))
                    .queue();
        } catch (Exception e) {
            if (e.getMessage().contains("404")) {
                event.getHook().sendMessage(String.format("Summoner ***%s*** not found.", summonerName)).queue();
            } else {
                event.getHook().sendMessage("An error occurred.").queue();
            }
        }

    }

}
