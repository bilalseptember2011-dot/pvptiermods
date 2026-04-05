package com.pvptier.tiering;

import com.pvptier.data.KitStats;

public class TierSystem {
    public static String getTier(int rating) {
        if (rating < 1000) return "Bronze";
        if (rating < 1200) return "Silver";
        if (rating < 1400) return "Gold";
        if (rating < 1600) return "Platinum";
        if (rating < 1800) return "Diamond";
        return "Master";
    }

    public static void updateRating(KitStats stats, boolean won, int matchesPlayed) {
        int expected = matchesPlayed > 0 ? (int)(1500 + Math.log10(matchesPlayed) * 50) : 1500;
        int kFactor = Math.min(50, 10 + matchesPlayed);
        double performance = (double)stats.totalHits / Math.max(1, stats.totalAttacks);
        double critBonus = (double)stats.crits / Math.max(1, stats.totalHits) * 20;
        double comboBonus = stats.maxCombo > 3 ? 15 : 0;

        int delta = (int)((won ? 1 : 0) - expected / 3000 + (performance * 30) + critBonus + comboBonus);
        stats.rating += kFactor * Math.signum(delta) * Math.abs(delta);
        stats.rating = Math.max(500, Math.min(3000, stats.rating));
    }
}
