package com.alistats.discorki.model;

import java.time.LocalDateTime;
import java.util.HashMap;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "ranks")
public class Rank implements Comparable<Rank> {
    // 400 lp in 1 division
    public static final Integer DIVISION_VALUE = 400;
    // 100 lp in 1 tier
    public static final Integer TIER_VALUE = 100;
    // apex tiers dont have divisions and their lp doesnt go to 0 with a promotion.
    // for example: a master would have 300lp, a grandmaster would have 600, and a
    // challenger would have 1100
    public static final Integer APEX_TIER_VALUE = 6 * DIVISION_VALUE;

    public enum Tier {
        IRON,
        BRONZE,
        SILVER,
        GOLD,
        PLATINUM,
        DIAMOND,
        MASTER,
        GRANDMASTER,
        CHALLENGER;
    }

    public enum Division {
        IV, III, II, I
    }

    public static HashMap<Division, Integer> divisionIntegers = new HashMap<Division, Integer>() {
        {
            put(Division.IV, 4);
            put(Division.III, 3);
            put(Division.II, 2);
            put(Division.I, 1);
        }
    };

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name = "summoner_id", nullable = false)
    private Summoner summoner;
    private String queueType;
    private Tier tier;
    private Division division;
    private Integer leaguePoints;
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    public static Integer getTotalLp(Rank rank) {
        Integer value = 0;

        value += getTotalLpFromDivision(rank.getDivision());
        value += getTierValueFromDivision(rank.getTier());
        value += rank.getLeaguePoints();

        return value;
    }

    // TODO: apex tiers have other lp gains
    private static Integer getTotalLpFromDivision(Division division) {
        return Division.valueOf(division.toString()).ordinal() * DIVISION_VALUE;
    }

    // TODO: apex tiers have other lp gains
    private static Integer getTierValueFromDivision(Tier tier) {
        return Tier.valueOf(tier.toString()).ordinal() * TIER_VALUE;
    }

    @Override
    public String toString() {
        return "Rank [id=" + id + ", summoner=" + summoner + ", queueType=" + queueType + ", tier=" + tier
                + ", division=" + division + ", leaguePoints=" + leaguePoints + ", createdAt=" + createdAt + "]";
    }

    @Override
    public int compareTo(Rank rank) {
        if (this.getTier().ordinal() > rank.getTier().ordinal()) {
            return 1;
        }

        if (this.getTier().ordinal() < rank.getTier().ordinal()) {
            return -1;
        }

        if (this.getDivision().ordinal() > rank.getDivision().ordinal()) {
            return 1;
        }

        if (this.getDivision().ordinal() < rank.getDivision().ordinal()) {
            return -1;
        }

        if (this.getLeaguePoints() > rank.getLeaguePoints()) {
            return 1;
        }

        if (this.getLeaguePoints() < rank.getLeaguePoints()) {
            return -1;
        }

        return 0;
    }
}
