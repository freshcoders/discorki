package com.alistats.discorki.discord.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.alistats.discorki.discord.command.shared.AbstractCommand;
import com.alistats.discorki.discord.command.shared.Command;
import com.alistats.discorki.riot.dto.constants.ChampionDto;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Component
public class Aram extends AbstractCommand implements Command {

    @Override
    public String getCommandName() {
        return "aram";
    }

    public void run(SlashCommandInteractionEvent event) {
        final int DEFAULT_ARAM_CHAMPS_PER_PLAYER = 3;

        // Get all players
        String playersConcatenated = event.getOption("other-players").getAsString();
        String[] players = playersConcatenated.split(",");

        User captain1 = event.getUser();
        User captain2 = event.getOption("other-captain").getAsUser();

        // Check if player size is in bounds
        if (players.length < 2 || players.length > 10) {
            event.getHook().sendMessage("Number of players must be between 2 and 10.").queue();
            return;
        }

        // Get total number of players
        int playerCount = players.length + 2;

        // Get random champions per player
        int championAmount = DEFAULT_ARAM_CHAMPS_PER_PLAYER;
        if (event.getOption("champion-amount") != null) {
            // Check if in bounds
            championAmount = event.getOption("champion-amount").getAsInt();
            if (championAmount < 1 || championAmount > 20) {
                event.getHook().sendMessage("Number of champions must be between 1 and 20.").queue();
                return;
            }
        }

        // Get total number of champions needed
        int totalChampionCount = playerCount * championAmount;

        Set<String> championNames;
        // Check if a specific champion class was requested
        if (event.getOption("champion-class") != null) {
            String championClass = event.getOption("champion-class").getAsString();
            try {
                championNames = gameConstantsController.getChampionNamesByClass(ChampionDto.Champion.Class.valueOf(championClass));
            } catch (IllegalArgumentException e) {
                event.getHook().sendMessage("Invalid champion class.").queue();
                return;
            }
        } else {
            championNames = gameConstantsController.getChampionNames();
        }

        // Get random champions
        List<String> randomChampions = championNames.stream()
                .collect(Collectors.collectingAndThen(Collectors.toList(), collected -> {
                    Collections.shuffle(collected);
                    return collected.stream();
                }))
                .limit(totalChampionCount)
                .collect(Collectors.toList());

        // Shuffle players and trim whitespace
        List<String> shuffledPlayers = Arrays.stream(players)
                .map(String::trim)
                .collect(Collectors.toList());
        Collections.shuffle(shuffledPlayers);

        LinkedHashMap<String, Set<String>> team1 = new LinkedHashMap<>();
        LinkedHashMap<String, Set<String>> team2 = new LinkedHashMap<>();

        int half = shuffledPlayers.size() / 2;
        int randomBinaryNumber = shuffledPlayers.size() % 2 == 1 ? new Random().nextInt(2) : 0;
        
        // Add players to teams, if randomBinaryNumber is 0, team1 gets the extra player
        team1.put(captain1.getName(), new HashSet<>());
        team2.put(captain2.getName(), new HashSet<>());
        for (int i = 0; i < half; i++) {
            team1.put(shuffledPlayers.get(i), new HashSet<>());
            team2.put(shuffledPlayers.get(i + half + randomBinaryNumber), new HashSet<>());
        }

        // Add champions to teams
        for (int i = 0; i < championAmount; i++) {
            for (String player : team1.keySet()) {
                team1.get(player).add(randomChampions.get(0));
                randomChampions.remove(0);
            }
            for (String player : team2.keySet()) {
                team2.get(player).add(randomChampions.get(0));
                randomChampions.remove(0);
            }
        }

        // Generate random binary number to decide which team gets blue side
        randomBinaryNumber = new Random().nextInt(2);

        // Send messages to team captains
        String team1Message = buildTeamMessage(team1, randomBinaryNumber == 0);
        String team2Message = buildTeamMessage(team2, randomBinaryNumber == 1);
        event.getJDA().retrieveUserById(captain1.getId()).queue(user -> {
            user.openPrivateChannel().queue(privateChannel -> {
                privateChannel.sendMessage(team1Message).queue();
            });
        });
        event.getJDA().retrieveUserById(captain2.getId()).queue(user -> {
            user.openPrivateChannel().queue(privateChannel -> {
                privateChannel.sendMessage(team2Message).queue();
            });
        });

        // Send message to channel with both teams without champions
        MessageEmbed embed = build(team1, team2, captain1, captain2);
        event.getHook().sendMessageEmbeds(embed).queue();
    }

    private MessageEmbed build(LinkedHashMap<String, Set<String>> teamBlue, LinkedHashMap<String, Set<String>> teamRed,
            User captain1, User captain2) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("ARAM teams generated!");
        builder.setDescription(String.format(
                "<@%s> and <@%s>, please check your DMs for the champion pools. Post them in lobby when the game starts!\r\n\r\n*Some rules we like to use: No exhaust. Trading allowed.*",
                captain1.getId(), captain2.getId()));
        builder.addField("Blue side", String.join("\r\n", teamBlue.keySet()), true);
        builder.addField("Red side", String.join("\r\n", teamRed.keySet()), true);
        int EMBED_COLOR = 5814783;
        builder.setColor(EMBED_COLOR);
        int ARAM_MAP_ID = 12;
        builder.setThumbnail(imageService.getMapUrl(ARAM_MAP_ID).toString());

        return builder.build();
    }

    private String buildTeamMessage(LinkedHashMap<String, Set<String>> team, boolean isBlueSide) {
        StringBuilder sb = new StringBuilder();
        sb.append("Your team is on **")
                .append(isBlueSide ? "Blue" : "Red")
                .append("** side. Here are your champion pools:\r\n\r\n");
        sb.append("```");
        for (Map.Entry<String, Set<String>> player : team.entrySet()) {
            sb.append(player.getKey())
                    .append(": ")
                    .append(String.join(", ", player.getValue()))
                    .append("\r\n");
        }
        sb.append("```\r\n*Paste the text above in the champion select lobby!*");

        return sb.toString();
    }
}
