package com.alistats.discorki.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name= "league")
public class League implements Comparable<League> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)    
    @Column(name = "id")
    private long id;
    @OneToOne(mappedBy = "league")
    private Rank rank;
    private Division division;
    private Tier tier;

    @Override
    public int compareTo(League league) {
        int thisValue = this.tier.getTierLpValue() + this.division.getDivisionLpValue();
        int otherValue = league.tier.getTierLpValue() + league.division.getDivisionLpValue();
        return thisValue - otherValue;    
    }

    public String getName() {
        return this.tier.getName() + this.division.getName();
    }
}
