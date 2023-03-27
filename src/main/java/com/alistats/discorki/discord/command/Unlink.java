package com.alistats.discorki.discord.command;

import com.alistats.discorki.discord.command.shared.AbstractCommand;
import com.alistats.discorki.discord.command.shared.Command;
import com.alistats.discorki.model.Player;
import com.alistats.discorki.model.Summoner;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Optional;

import org.springframework.stereotype.Component;

@Component
public class Unlink extends AbstractCommand implements Command {
    @Override
    public String getCommandName() {
        return "unlink";
    }
    
    @SuppressWarnings("null")
    // Suppressed because the options are required, so it will never be null
    public void run(SlashCommandInteractionEvent event) {
        // unlink a summoner from a user
        Optional<Player> userOpt = playerRepo.findById(event.getOption("discord-username").getAsUser().getId());
        if (userOpt.isEmpty()) {
            event.getHook().sendMessage("User not found.").queue();
            return;
        }
        Optional<Summoner> summonerOpt = summonerRepo.findByName(event.getOption("league-username").getAsString());
        if (summonerOpt.isEmpty()) {
            event.getHook().sendMessage("Summoner not found.").queue();
            return;
        }
        Player player = userOpt.get();
        Summoner summoner = summonerOpt.get();

        player.removeSummonerById(summoner.getId());
        summoner.removeLinkedPlayerById(player.getId());
        playerRepo.save(player);
        summonerRepo.save(summoner);
        event.getHook().sendMessage(String.format("Unlinked ***%s*** from <@%s>.", summoner.getName(), player.getId()))
                .queue();
    }
}
