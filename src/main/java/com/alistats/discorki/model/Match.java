package com.alistats.discorki.model;

import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
@Table(name= "matches")
public class Match {
    public enum Status {
        IN_PROGRESS, FINISHED
    }

    @Id
    private Long id;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "match_summoner", 
        joinColumns = @JoinColumn(name = "match_id"), 
        inverseJoinColumns = @JoinColumn(name = "summoner_id")
    )
    private Set<Summoner> trackedSummoners;
    private Status status;
    private Long gameQueueConfigId;
}
