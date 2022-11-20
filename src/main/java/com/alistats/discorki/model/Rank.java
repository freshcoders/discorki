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
public class Rank implements Comparable<Rank> {
    public static final Integer DIVISION_VALUE = 400; // 400 lp in 1 division
    public static final Integer TIER_VALUE = 100; // 100 lp in 1 tier

    public enum Tier {
        IRON,
        BRONZE,
        SILVER,
        GOLD,
        PLATINUM,
        DIAMOND,
        MASTER,
        GRANDMASTER,
        CHALLENGER
    }

    public enum Division {
        IV, III, II, I
    }

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name="summoner_id", nullable=false)
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

    // TODO: change to map
    public static Integer divisionToInteger(Division division) {
        // Convert the roman numeral to an integer
        switch (division) {
            case I:
                return 1;
            case II:
                return 2;
            case III:
                return 3;
            case IV:
                return 4;
            default:
                return null;
        }
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

        return 0;
    }
}
