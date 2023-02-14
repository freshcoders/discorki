package com.alistats.discorki.model;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
    @OneToMany(mappedBy = "server", fetch = FetchType.EAGER)
    private Set<Player> players = new HashSet<>();
    private boolean active = true;
    private long defaultChannelId;

    public Optional<Player> getUserInGuildByUserId(String id) {
        return players.stream().filter(user -> user.getId().equals(id)).findFirst();
    }


    public Set<Summoner> getSummoners() {
        return players.stream()
            .flatMap(user -> user.getSummoners().stream())
            .collect(Collectors.toSet());
    }
}
