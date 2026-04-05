package com.pvptier.data;

import com.pvptier.enums.PvpKit;
import java.util.HashMap;
import java.util.Map;

public class PlayerData {
    public int matchThreshold = 5;
    public Map<String, KitStats> kits = new HashMap<>();

    public KitStats getKitStats(PvpKit kit) {
        String id = kit.toString();
        return kits.computeIfAbsent(id, k -> new KitStats());
    }

    public void resetKit(PvpKit kit) {
        kits.remove(kit.toString());
    }

    public void resetAll() {
        kits.clear();
    }
}
