package com.pvptier.ai;

import com.pvptier.data.KitStats;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AiCoach {
    // Replace with your secure API endpoint. Do NOT hardcode keys.
    private static final String AI_ENDPOINT = System.getenv("PVP_AI_ENDPOINT");

    public static void giveRecommendation(ServerPlayerEntity player, KitStats stats) {
        if (AI_ENDPOINT == null) {
            player.sendMessage(Text.literal("AI Coach disabled. Set PVP_AI_ENDPOINT env var to enable."));
            return;
        }

        String prompt = String.format(
            "PvP Stats -> Hit Consistency: %.1f%%, Combos: %d, Crits: %d, Opponent Hits Taken: %d, Rating: %d. Give 3 short actionable tips.",
            (double)stats.totalHits / Math.max(1, stats.totalAttacks) * 100,
            stats.maxCombo, stats.crits, stats.opponentHits, stats.rating
        );

        // Async HTTP call to your AI backend (OpenAI, Ollama, etc.)
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(AI_ENDPOINT))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"prompt\":\"" + prompt + "\"}"))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(res -> {
                    String reply = res.body();
                    player.sendMessage(Text.literal("🤖 AI Coach: " + reply));
                }).exceptionally(e -> {
                    player.sendMessage(Text.literal("❌ AI Coach error: " + e.getMessage()));
                    return null;
                });
    }
                            }
