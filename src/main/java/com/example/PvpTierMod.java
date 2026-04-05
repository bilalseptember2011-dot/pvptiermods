package com.pvptier;

import com.pvptier.data.PlayerDataManager;
import com.pvptier.tracker.CombatTracker;
import com.pvptier.commands.PvPCommands;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class PvPTierMod implements ModInitializer {
    public static final String MOD_ID = "pvptiermod";

    @Override
    public void onInitialize() {
        // Register commands
        CommandRegistrationCallback.EVENT.register(PvPCommands::register);

        // Register combat tracking
        ServerEntityEvents.ENTITY_DAMAGE.register((target, damageSource, amount) -> {
            CombatTracker.handleDamage(target, damageSource, amount);
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            PlayerDataManager.loadData(handler.player);
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            PlayerDataManager.saveData(handler.player);
        });
    }
}
