package com.alistats.discorki.discord.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.alistats.discorki.discord.command.shared.AbstractCommand;
import com.alistats.discorki.discord.command.shared.Command;
import com.alistats.discorki.model.Player;
import com.alistats.discorki.model.QueueType;
import com.alistats.discorki.model.Rank;
import com.alistats.discorki.model.Server;
import com.alistats.discorki.model.Summoner;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Component
public class Leaderboard extends AbstractCommand implements Command {
    @Override
    public String getCommandName() {
        return "leaderboard";
    }

    public void run(SlashCommandInteractionEvent event) {
        Server server = obtainServer(event.getGuild());

        Set<Rank> ranks = new HashSet<>();

        // Get the latest ranks for soloq and flexq of all summoners in guild
        Set<Player> players = server.getPlayers();
        for (Player player : players) {
            Set<Summoner> summoners = player.getSummoners();
            for (Summoner summoner : summoners) {
                Rank soloqRank = summoner.getCurrentRank(QueueType.RANKED_SOLO_5x5);
                Rank flexqRank = summoner.getCurrentRank(QueueType.RANKED_FLEX_SR);
                if (soloqRank != null) {
                    ranks.add(soloqRank);
                }
                if (flexqRank != null) {
                    ranks.add(flexqRank);
                }
            }
        }

        reply(event, build(ranks));
    }

    public String build(Set<Rank> ranks) {
        // Divide the ranks into soloq and flex
        ArrayList<Rank> soloqRanks = new ArrayList<>();
        ArrayList<Rank> flexRanks = new ArrayList<>();
        for (Rank rank : ranks) {
            if (rank.getQueueType().name().equals("RANKED_SOLO_5x5")) {
                soloqRanks.add(rank);
            } else if (rank.getQueueType().name().equals("RANKED_FLEX_SR")) {
                flexRanks.add(rank);
            }
        }

        // If there are no ranks, return a message
        if (soloqRanks.size() == 0 && flexRanks.size() == 0) {
            return "No ranks found";
        }

        // Sort the ranks
        soloqRanks.sort(Collections.reverseOrder());
        flexRanks.sort(Collections.reverseOrder());

        // Build the string
        String sb = "\n__***Leaderboards***__\n**Solo queue**\n" +
                buildQueueSegment(soloqRanks) +
                "\n**Flex queue**\n" +
                buildQueueSegment(flexRanks);

        return sb;
    }

    public String buildQueueSegment(ArrayList<Rank> ranks) {
        StringBuilder sb = new StringBuilder();
        for (Rank rank : ranks) {
            sb.append(rank.getSummoner().getName())
                    .append(" - ")
                    .append(buildRankFieldLine(rank));
        }
        return sb.toString();
    }

    public static String buildRankFieldLine(Rank rank) {
        String str = rank.getLeague().getTier().getEmoji() +
                " " +
                rank.getLeague().getTier() +
                " " +
                rank.getLeague().getDivision() +
                " - " +
                rank.getLeaguePoints() +
                "LP\n";

        return str;
    }
}
