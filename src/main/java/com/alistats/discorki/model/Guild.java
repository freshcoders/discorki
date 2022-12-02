package com.alistats.discorki.model;

import java.util.List;

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
@Table(name= "guilds")
// https://discord.com/developers/docs/resources/guild
public class Guild {
    @Id
    private String id;
    private String name;
    @OneToMany(mappedBy = "guild")
    private List<User> users;
}
