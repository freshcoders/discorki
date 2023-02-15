package com.alistats.discorki.discord.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(readOnly = true)
    public void run(SlashCommandInteractionEvent event) {
        Server server = obtainServer(event.getGuild());

        Set<Rank> ranks = new HashSet<>();

        Hibernate.initialize(server.getPlayers());

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

    private String build(Set<Rank> ranks) {
        // Divide the ranks into soloq and flex
        ArrayList<Rank> soloqRanks = new ArrayList<>();
        ArrayList<Rank> flexRanks = new ArrayList<>();
        for (Rank rank : ranks) {
            if (rank.getQueueType() == QueueType.RANKED_SOLO_5x5) {
                soloqRanks.add(rank);
            } else if (rank.getQueueType() == QueueType.RANKED_FLEX_SR) {
                flexRanks.add(rank);
            }
        }

        // If there are no ranks, return a message
        if (soloqRanks.isEmpty() && flexRanks.isEmpty()) {
            return "No ranks found";
        }

        // Sort the ranks
        Collections.sort(soloqRanks, Collections.reverseOrder());
        Collections.sort(flexRanks, Collections.reverseOrder());

        // Build the string
        StringBuilder sb = new StringBuilder();
        if (!soloqRanks.isEmpty()) {
            sb.append("__***Leaderboards***__\n**Solo queue**\n");
            buildQueueSegment(sb, soloqRanks);
        }
        if (!flexRanks.isEmpty()) {
            sb.append("\n**Flex queue**\n");
            buildQueueSegment(sb, flexRanks);
        }

        return sb.toString();
    }

    private void buildQueueSegment(StringBuilder sb, ArrayList<Rank> ranks) {
        for (Rank rank : ranks) {
            sb.append(rank.getSummoner().getName())
                    .append(" - ");
            buildRankFieldLine(sb, rank);
        }
    }

    private void buildRankFieldLine(StringBuilder sb, Rank rank) {
        sb.append(rank.getLeague().getTier().getEmoji())
                .append(" ")
                .append(rank.getLeague().getTier())
                .append(" ")
                .append(rank.getLeague().getDivision())
                .append(" - ")
                .append(rank.getLeaguePoints())
                .append("LP\n");
    }
}
