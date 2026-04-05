package com.pvptier.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.pvptier.data.PlayerData;
import com.pvptier.data.PlayerDataManager;
import com.pvptier.data.KitStats;
import com.pvptier.enums.PvpKit;
import com.pvptier.tiering.TierSystem;
import com.pvptier.ai.AiCoach;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class PvPCommands {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        dispatcher.register(CommandManager.literal("pvptier")
            .then(CommandManager.literal("status")
                .executes(ctx -> {
                    ServerPlayerEntity p = ctx.getSource().getPlayerOrThrow();
                    PlayerData data = PlayerDataManager.getData(p);
                    KitStats stats = data.getKitStats(PvpKit.SWORD);
                    String tier = TierSystem.getTier(stats.rating);
                    p.sendMessage(Text.literal("📊 Kit: SWORD | Tier: " + tier + " (" + stats.rating + ") | Matches: " + stats.matches +
                        " | Hit%: " + (stats.totalHits * 100 / Math.max(1, stats.totalAttacks)) + "%"));
                    return 1;
                }))
            .then(CommandManager.literal("set_matches")
                .then(CommandManager.argument("count", IntegerArgumentType.integer(1, 50))
                    .executes(ctx -> {
                        int count = IntegerArgumentType.getInteger(ctx, "count");
                        PlayerData data = PlayerDataManager.loadData(ctx.getSource().getPlayerOrThrow());
                        data.matchThreshold = count;
                        PlayerDataManager.saveData(ctx.getSource().getPlayerOrThrow(), data);
                        ctx.getSource().getPlayerOrThrow().sendMessage(Text.literal("✅ Match threshold set to " + count));
                        return 1;
                    })))
            .then(CommandManager.literal("reset")
                .then(CommandManager.argument("kit", StringArgumentType.string())
                    .suggests((ctx, builder) -> {
                        for (PvpKit k : PvpKit.values()) builder.suggest(k.name());
                        return builder.buildFuture();
                    })
                    .executes(ctx -> {
                        String kitName = StringArgumentType.getString(ctx, "kit");
                        PvpKit kit = PvpKit.valueOf(kitName.toUpperCase());
                        PlayerData data = PlayerDataManager.loadData(ctx.getSource().getPlayerOrThrow());
                        data.resetKit(kit);
                        PlayerDataManager.saveData(ctx.getSource().getPlayerOrThrow(), data);
                        ctx.getSource().getPlayerOrThrow().sendMessage(Text.literal("🗑️ Reset " + kit + " tier & stats."));
                        return 1;
                    }))
                .executes(ctx -> {
                    PlayerData data = PlayerDataManager.loadData(ctx.getSource().getPlayerOrThrow());
                    data.resetAll();
                    PlayerDataManager.saveData(ctx.getSource().getPlayerOrThrow(), data);
                    ctx.getSource().getPlayerOrThrow().sendMessage(Text.literal("🗑️ All kits reset."));
                    return 1;
                }))
            .then(CommandManager.literal("coach")
                .executes(ctx -> {
                    ServerPlayerEntity p = ctx.getSource().getPlayerOrThrow();
                    PlayerData data = PlayerDataManager.getData(p);
                    AiCoach.giveRecommendation(p, data.getKitStats(PvpKit.SWORD));
                    return 1;
                })));
    }
                              }
