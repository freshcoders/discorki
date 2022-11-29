package com.alistats.discorki.model;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name= "matches")
public class Match {
    @Id
    private Long id;
    @ManyToMany
    @JoinTable(
        name = "match_summoner", 
        joinColumns = @JoinColumn(name = "match_id"), 
        inverseJoinColumns = @JoinColumn(name = "summoner_id"))
    private Set<Summoner> trackedSummoners;
}
