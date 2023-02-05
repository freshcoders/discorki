package com.alistats.discorki.model;

import lombok.Getter;

@Getter
public enum Tier {
    IRON(0, false, "🟤", "Iron"),
    BRONZE(1, false, "🟠", "Bronze"),
    SILVER(2, false, "⚪", "Silver"),
    GOLD(3, false, "🟡", "Gold"),
    PLATINUM(4, false, "🟢", "Platinum"),
    DIAMOND(5,false, "🔵", "Diamond"),
    MASTER(6, true, "🟣", "Master"),
    GRANDMASTER(7, true, "⭕", "Grandmaster"),
    CHALLENGER(8, true, "🔴", "Challenger");

    private final int tierLevel;
    private final boolean isApex;
    private final String emoji;
    private final String fancyName;

    private static final int LP_VALUE_PER_TIER = 400;
    private static final int NORMAL_TIERS = 6;
    
    Tier(int tierLevel, boolean isApex, String emoji, String fancyName) {
        this.tierLevel = tierLevel;
        this.isApex = isApex;
        this.emoji = emoji;
        this.fancyName = fancyName;
    }

    public int getTierLpValue() {
        if (!this.isApex) {
            return this.tierLevel * LP_VALUE_PER_TIER;
        } else {
            return NORMAL_TIERS * LP_VALUE_PER_TIER;
        }
    }

    public String getName() {
        return this.name().substring(0, 1).toUpperCase() + this.name().substring(1).toLowerCase();
    }
}
