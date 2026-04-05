package com.pvptier.tracker;

import com.pvptier.data.PlayerData;
import com.pvptier.data.PlayerDataManager;
import com.pvptier.data.KitStats;
import com.pvptier.enums.PvpKit;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public class CombatTracker {
    public static void handleDamage(LivingEntity target, DamageSource source, float amount) {
        if (!(target instanceof ServerPlayerEntity player)) return;
        if (!(source.getAttacker() instanceof PlayerEntity attacker)) return;

        PlayerData data = PlayerDataManager.loadData(player);
        KitStats stats = data.getKitStats(PvpKit.SWORD); // Auto-switch based on held item later

        int currentTime = (int) (System.currentTimeMillis() / 1000);
        if (!stats.inMatch) {
            stats.inMatch = true;
            stats.currentCombo = 0;
        }
        stats.lastDamageTime = currentTime;

        stats.totalAttacks++;
        stats.totalHits++;
        stats.currentCombo++;
        stats.maxCombo = Math.max(stats.maxCombo, stats.currentCombo);

        // Crit detection (Fabric doesn't expose crit flag directly, approximate via damage multiplier)
        if (amount > 3.5f && source.getAttacker() instanceof PlayerEntity) stats.crits++;

        // Opponent tracking
        if (target instanceof ServerPlayerEntity p) {
            PlayerData oppData = PlayerDataManager.loadData(p);
            oppData.getKitStats(PvpKit.SWORD).opponentHits++;
        }
    }
}
