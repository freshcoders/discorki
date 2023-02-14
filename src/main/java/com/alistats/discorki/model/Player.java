package com.alistats.discorki.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.entities.User;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name= "players")
public class Player {
    @Id
    private String id;
    private String username;
    private String discriminator;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "player_summoner", 
        joinColumns = @JoinColumn(name = "player_id"), 
        inverseJoinColumns = @JoinColumn(name = "summoner_id")
    )
    private Set<Summoner> summoners = new HashSet<>();
    @ManyToOne
    @JoinColumn(name = "guild_id", nullable = false)
    private Server server;

    public void addSummoner(Summoner summoner) {
        summoners.add(summoner);
    }

    public void removeSummonerById(String id) {
        summoners.removeIf(summoner -> summoner.getId().equals(id));
    }

    public boolean hasSummonerByName(String name) {
        return summoners.stream().anyMatch(summoner -> summoner.getName().equals(name));
    }

    public Player(User user) {
        this.id = user.getId();
        this.username = user.getName();
        this.discriminator = user.getDiscriminator();
    }
}
