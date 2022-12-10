package com.alistats.discorki.model;

public enum Tier {
    IRON(0, TierType.NORMAL),
    BRONZE(1, TierType.NORMAL),
    SILVER(2, TierType.NORMAL),
    GOLD(3, TierType.NORMAL),
    PLATINUM(4, TierType.NORMAL),
    DIAMOND(5, TierType.NORMAL),
    MASTER(6, TierType.APEX),
    GRANDMASTER(7, TierType.APEX),
    CHALLENGER(8, TierType.APEX);

    private Integer tierLevel;
    private TierType tierType;

    private static final Integer LP_VALUE_PER_TIER = 400;
    private static final Integer NORMAL_TIERS = 6;
    
    private Tier(Integer tierLevel, TierType tierType) {
        this.tierLevel = tierLevel;
        this.tierType = tierType;
    }

    public Integer getTierLpValue() {
        if (tierType == TierType.NORMAL) {
            return this.tierLevel * LP_VALUE_PER_TIER;
        } else {
            return NORMAL_TIERS * LP_VALUE_PER_TIER;
        }
    }
}
