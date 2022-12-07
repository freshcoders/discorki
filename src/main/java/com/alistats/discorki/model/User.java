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

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name= "users")
// https://discord.com/developers/docs/resources/user
public class User {
    @Id
    private String id;
    private String username;
    private String discriminator;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_summoner", 
        joinColumns = @JoinColumn(name = "user_id"), 
        inverseJoinColumns = @JoinColumn(name = "summoner_id")
    )
    private Set<Summoner> summoners = new HashSet<>();
    @ManyToOne
    @JoinColumn(name = "guild_id", nullable = false)
    private Guild guild;

    public void addSummoner(Summoner summoner) {
        summoners.add(summoner);
    }
}
