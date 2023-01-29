package com.alistats.discorki.discord.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.alistats.discorki.model.Rank;

@Component
public class LeaderboardCommandView {
    public String build(Set<Rank> ranks) {
        // Divide the ranks into soloq and flex
        ArrayList<Rank> soloqRanks = new ArrayList<Rank>();
        ArrayList<Rank> flexRanks = new ArrayList<Rank>();
        for (Rank rank : ranks) {
            if (rank.getQueueType().equals("RANKED_SOLO_5x5")) {
                soloqRanks.add(rank);
            } else if (rank.getQueueType().equals("RANKED_FLEX_SR")) {
                flexRanks.add(rank);
            }
        }

        // If there are no ranks, return a message
        if (soloqRanks.size() == 0 && flexRanks.size() == 0) {
            return "No ranks found";
        }

        // Sort the ranks
        Collections.sort(soloqRanks, Collections.reverseOrder());
        Collections.sort(flexRanks, Collections.reverseOrder());

        // Build the string
        StringBuilder sb = new StringBuilder();
        sb.append("\r\n__***Leaderboards***__\r\n**Solo queue**\r\n")
                .append(buildQueueSegment(soloqRanks))
                .append("\r\n**Flex queue**\r\n")
                .append(buildQueueSegment(flexRanks));

        return sb.toString();
    }

    public String buildQueueSegment(ArrayList<Rank> ranks) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ranks.size(); i++) {
            Rank rank = ranks.get(i);
            sb.append(rank.getSummoner().getName())
                    .append(" - ")
                    .append(buildRankFieldLine(rank));
        }
        return sb.toString();
    }

    public static String buildRankFieldLine(Rank rank) {
        StringBuilder str = new StringBuilder();
        str.append(rank.getLeague().getTier().getEmoji())
                .append(" ")
                .append(rank.getLeague().getTier())
                .append(" ")
                .append(rank.getLeague().getDivision())
                .append(" - ")
                .append(rank.getLeaguePoints())
                .append("LP\n");

        return str.toString();
    }
}
