package com.alistats.discorki.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

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
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "match_summoner", 
        joinColumns = @JoinColumn(name = "match_id"), 
        inverseJoinColumns = @JoinColumn(name = "summoner_id")
    )
    private List<Summoner> trackedSummoners;
    private Status status;
}
