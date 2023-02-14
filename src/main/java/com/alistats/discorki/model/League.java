package com.alistats.discorki.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class League implements Comparable<League> {
    private Division division;
    private Tier tier;

    @Override
    public int compareTo(League league) {
        int thisValue = this.tier.getTierLpValue() + this.division.getDivisionLpValue();
        int otherValue = league.tier.getTierLpValue() + league.division.getDivisionLpValue();
        return thisValue - otherValue;    
    }

    public String getName() {
        return this.tier.getName() + this.division.name();
    }
}
