package com.alistats.discorki.model;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Getter;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class EmbedContainer {
    final Set<SummonerContainer> summonerContainers = new HashSet<>();

    public void addPersonalEmbed(Summoner summoner, MessageEmbed embed) {
        SummonerContainer summonerContainer = getSummonerContainer(summoner);
        summonerContainer.addPersonalEmbed(embed);
    }

    public void addTeamEmbed(Summoner summoner, MessageEmbed embed) {
        SummonerContainer summonerContainer = getSummonerContainer(summoner);
        summonerContainer.addTeamEmbed(embed);
    }

    public void addTeamEmbeds(Summoner summoner, Set<MessageEmbed> embeds) {
        SummonerContainer summonerContainer = getSummonerContainer(summoner);
        summonerContainer.addTeamEmbeds(embeds);
    }

    public boolean isEmpty() {
        return summonerContainers.isEmpty();
    }

    public Set<Guild> getGuilds() {
        return getSummoners().stream()
                .flatMap(summoner -> summoner.getUsers().stream())
                .map(User::getGuild)
                .collect(Collectors.toSet());
    }

    public boolean guildHasTeamEmbeds(Guild guild) {
        return getSummoners().stream()
                .filter(summoner -> summoner.getUsers().stream()
                        .anyMatch(user -> user.getGuild().equals(guild)))
                .anyMatch(summoner -> getSummonerContainer(summoner).hasTeamEmbeds());
    }

    public boolean hasTeamEmbeds() {
        return getSummoners().stream()
                .anyMatch(summoner -> getSummonerContainer(summoner).hasTeamEmbeds());
    }

    public Set<MessageEmbed> getGuildEmbeds(Guild guild) {
        Set<MessageEmbed> embeds = new HashSet<>();

        getSummoners().stream()
                .filter(summoner -> summoner.getUsers().stream()
                        .anyMatch(user -> user.getGuild().equals(guild)))
                .forEach(summoner -> {
                    SummonerContainer summonerContainer = getSummonerContainer(summoner);
                    embeds.addAll(summonerContainer.getPeronalEmbeds());
                    embeds.addAll(summonerContainer.getTeamEmbeds());
                });

        return embeds;
    }

    private Set<Summoner> getSummoners() {
        Set<Summoner> summoners = new HashSet<>();

        summonerContainers.forEach(e -> summoners.add(e.getSummoner()));

        return summoners;
    }

    private SummonerContainer getSummonerContainer(Summoner summoner) {
        return summonerContainers.stream()
                .filter(e -> e.getSummoner().equals(summoner))
                .findFirst()
                .orElseGet(() -> {
                    SummonerContainer newSummonerContainer = new SummonerContainer(summoner);
                    summonerContainers.add(newSummonerContainer);
                    return newSummonerContainer;
                });
    }

    @Getter
    static
    class SummonerContainer {
        private final Summoner summoner;
        private final Set<MessageEmbed> peronalEmbeds = new HashSet<>();
        private final Set<MessageEmbed> teamEmbeds = new HashSet<>();
    
        public SummonerContainer(Summoner summoner) {
            this.summoner = summoner;
        }
    
        public void addPersonalEmbed(MessageEmbed embed) {
            peronalEmbeds.add(embed);
        }
    
        public void addTeamEmbeds(Set<MessageEmbed> embeds) {
            teamEmbeds.addAll(embeds);
        }
    
        public void addTeamEmbed(MessageEmbed embed) {
            teamEmbeds.add(embed);
        }
    
        public boolean hasTeamEmbeds() {
            return !teamEmbeds.isEmpty();
        }
    }
}
