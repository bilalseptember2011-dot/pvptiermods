package com.pvptier.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class PlayerDataManager {
    private static final Path CONFIG_DIR = Path.of("config", "pvptier");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static PlayerData loadData(ServerPlayerEntity player) {
        UUID id = player.getUuid();
        Path file = CONFIG_DIR.resolve(id + ".json");
        if (Files.exists(file)) {
            try (Reader reader = Files.newBufferedReader(file)) {
                return GSON.fromJson(reader, PlayerData.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        PlayerData data = new PlayerData();
        saveData(player, data);
        return data;
    }

    public static void saveData(ServerPlayerEntity player, PlayerData data) {
        try {
            Files.createDirectories(CONFIG_DIR);
            Files.writeString(CONFIG_DIR.resolve(player.getUuid() + ".json"), GSON.toJson(data));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static PlayerData getData(ServerPlayerEntity player) {
        UUID id = player.getUuid();
        Path file = CONFIG_DIR.resolve(id + ".json");
        if (Files.exists(file)) {
            try (Reader reader = Files.newBufferedReader(file)) {
                return GSON.fromJson(reader, PlayerData.class);
            } catch (Exception e) {
                return new PlayerData();
            }
        }
        return new PlayerData();
    }
}
