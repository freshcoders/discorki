package com.alistats.discorki.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class League implements Comparable<League> {
    private Division division;
    private Tier tier;

    @Override
    public int compareTo(League league) {
        Integer thisValue = this.division.getDivisionLpValue() + this.tier.getTierLpValue();
        Integer otherValue = league.division.getDivisionLpValue() + league.tier.getTierLpValue();
        return thisValue - otherValue;    
    }
}
