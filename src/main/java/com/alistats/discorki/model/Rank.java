package com.alistats.discorki.model;

import java.time.LocalDateTime;
import java.util.HashMap;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "ranks")
public class Rank implements Comparable<Rank> {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name = "summoner_id", nullable = false)
    private Summoner summoner;
    private String queueType;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "league_id", referencedColumnName = "id")
    private League league;
    private Integer leaguePoints;
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    private static final HashMap<Integer, String> queueIdAndTypeMap = new HashMap<Integer, String>() {
        {
            put(420, "RANKED_SOLO_5x5");
            put(440, "RANKED_FLEX_SR");
        }
    };

    public static String getQueueTypeByQueueId(Integer queueId) {
        return queueIdAndTypeMap.get(queueId);
    }

    public Integer getTotalLp() {
        Integer value = 0;

        value += league.getDivision().getDivisionLpValue();
        value += league.getTier().getTierLpValue();
        value += getLeaguePoints();

        return value;
    }

    @Override
    public int compareTo(Rank rank) {
        return this.getTotalLp() - rank.getTotalLp();
    }

}
