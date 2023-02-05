package com.alistats.discorki.discord.command;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.alistats.discorki.discord.command.shared.AbstractCommand;
import com.alistats.discorki.discord.command.shared.Command;
import com.alistats.discorki.model.Match;
import com.alistats.discorki.model.Match.Status;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Component
public class Debug extends AbstractCommand implements Command{
    @Override
    public String getCommandName() {
        return "debug";
    }

    public void run(SlashCommandInteractionEvent event) {
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
}
