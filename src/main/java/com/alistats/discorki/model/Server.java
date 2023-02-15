package com.alistats.discorki.model;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name= "servers")
public class Server {
    @Id
    private String id;
    private String name;
    @OneToMany(mappedBy = "server")
    private Set<Player> players = new HashSet<>();
    private boolean active = true;
    private long defaultChannelId;

    public Optional<Player> getPlayerById(Long playerId) {
        return players.stream().filter(player -> player.getId() == playerId).findFirst();
    }

    public Optional<Player> getPlayerByDiscordId(String discordId) {
        return players.stream().filter(player -> player.getDiscordId().equals(discordId)).findFirst();
    }

    public Set<Summoner> getSummoners() {
        return players.stream()
            .flatMap(user -> user.getSummoners().stream())
            .collect(Collectors.toSet());
    }
}
