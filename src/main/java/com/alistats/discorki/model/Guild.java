package com.alistats.discorki.model;

import java.util.Set;

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
@Table(name= "guilds")
// https://discord.com/developers/docs/resources/guild
public class Guild {
    @Id
    private String id;
    private String name;
    @OneToMany(mappedBy = "guild", fetch = FetchType.EAGER)
    private Set<User> users;

    public User getUserInGuildByUserId(String id) {
        return users.stream().filter(user -> user.getId().equals(id)).findFirst().orElse(null);
    }

}
