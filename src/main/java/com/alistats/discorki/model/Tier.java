package com.alistats.discorki.model;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;

@Getter
public enum Tier {
    IRON("🟤", false),
    BRONZE("🟠", false),
    SILVER("⚪", false),
    GOLD("🟡", false),
    PLATINUM("🟢", false),
    DIAMOND("🔵", false),
    MASTER("🟣", false),
    GRANDMASTER("⭕", false),
    CHALLENGER("🔴", false);

    private final boolean isApex;
    private final String emoji;

    private static final int LP_VALUE_PER_TIER = 400;
    private static final int NORMAL_TIERS = 6;
    
    Tier(String emoji, boolean isApex) {
        this.isApex = isApex;
        this.emoji = emoji;
    }

    public int getTierLpValue() {
        if (!this.isApex) {
            return this.ordinal() * LP_VALUE_PER_TIER;
        } else {
            return NORMAL_TIERS * LP_VALUE_PER_TIER;
        }
    }

    public String getName() {
        return StringUtils.upperCase(this.name());
    }
}
