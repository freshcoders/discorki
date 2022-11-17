package com.alistats.discorki.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "ranks")
public class Rank {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name="summoner_id", nullable=false)
    private Summoner summoner;
    private String queueType;
    private String tier;
    private String rank;
    private Integer leaguePoints;
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}
