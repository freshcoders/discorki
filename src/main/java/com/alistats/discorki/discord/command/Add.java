package com.alistats.discorki.discord.command;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.alistats.discorki.discord.command.shared.AbstractCommand;
import com.alistats.discorki.discord.command.shared.Command;
import com.alistats.discorki.model.Guild;
import com.alistats.discorki.model.Rank;
import com.alistats.discorki.model.Summoner;
import com.alistats.discorki.model.User;
import com.alistats.discorki.riot.dto.LeagueEntryDto;
import com.alistats.discorki.riot.dto.SummonerDto;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Component
public class Add extends AbstractCommand implements Command {

    @Override
    public String getCommandName() {
        return "add";
    }

    public void run(SlashCommandInteractionEvent event) {
        // Get guild
        Guild guild = getGuild(event.getGuild());
        net.dv8tion.jda.api.entities.User discordUser = event.getOption("discord-username").getAsUser();
        // Check if user is not a bot
        if (discordUser.isBot()) {
            event.getHook().sendMessage("Cannot link a bot.").queue();
            return;
        }

        String summonerName = event.getOption("league-username").getAsString();
        Optional<User> userOpt = guild.getUserInGuildByUserId(discordUser.getId());

        if (userOpt.isEmpty()) {
            // Create new user if not found
            User newUser = new User(discordUser);
            newUser.setGuild(guild);
            newUser = userRepo.save(newUser);
            userOpt = Optional.of(newUser);
        } else if (userOpt.get().hasSummonerByName(summonerName)) {
            event.getHook().sendMessage(
                    String.format("Summoner ***%s*** is already linked to <@%s>", summonerName, discordUser.getId()))
                    .queue();
            return;
        }

        User user = userOpt.get();

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
            LeagueEntryDto[] leagueEntryDtos = leagueApiController.getLeagueEntries(summoner.getId());

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
                event.getHook().sendMessage("An error occurred.").queue();
            }
        }

    }

}
