package com.alistats.discorki.model;

import lombok.Getter;

@Getter
public enum Tier {
    IRON(0, TierType.NORMAL, "🟤", "Iron"),
    BRONZE(1, TierType.NORMAL, "🟠", "Bronze"),
    SILVER(2, TierType.NORMAL, "⚪", "Silver"),
    GOLD(3, TierType.NORMAL, "🟡", "Gold"),
    PLATINUM(4, TierType.NORMAL, "🟢", "Plat."),
    DIAMOND(5, TierType.NORMAL, "🔵", "Diamond"),
    MASTER(6, TierType.APEX, "🟣", "Master"),
    GRANDMASTER(7, TierType.APEX, "⭕", "GM"),
    CHALLENGER(8, TierType.APEX, "🔴", "Chall.");

    private int tierLevel;
    private TierType tierType;
    private String emoji;
    private String shortName;

    private static final int LP_VALUE_PER_TIER = 400;
    private static final int NORMAL_TIERS = 6;
    
    private Tier(int tierLevel, TierType tierType, String emoji, String shortName) {
        this.tierLevel = tierLevel;
        this.tierType = tierType;
        this.emoji = emoji;
        this.shortName = shortName;
    }

    public int getTierLpValue() {
        if (tierType == TierType.NORMAL) {
            return this.tierLevel * LP_VALUE_PER_TIER;
        } else {
            return NORMAL_TIERS * LP_VALUE_PER_TIER;
        }
    }

    public String getName() {
        return this.name().substring(0, 1).toUpperCase() + this.name().substring(1).toLowerCase();
    }
}
