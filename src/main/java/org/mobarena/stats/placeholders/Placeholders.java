package org.mobarena.stats.placeholders;

import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;
import org.bukkit.OfflinePlayer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.mobarena.stats.MobArenaStatsPlugin;
import org.mobarena.stats.MobArenaStats;
import org.mobarena.stats.store.ArenaStats;
import org.mobarena.stats.store.GlobalStats;
import org.mobarena.stats.store.StatsStore;

public class Placeholders extends PlaceholderExpansion {

    MobArenaStats plugin = MobArenaStatsPlugin.getInstance();
    StatsStore store = plugin.getStatsStore();
    GlobalStats globalStats = store.getGlobalStats();

    @Override
    public String getAuthor() {
        return "Maroon28, Garbagemule";
    }

    @Override
    public String getIdentifier() {
        return "mobarenastats";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }
    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }

    public String onRequest(OfflinePlayer player, String identifier) {
        switch (identifier) {
            case "global_sessions":
                return String.valueOf(globalStats.totalSessions);
            case "global_duration":
                return String.valueOf(globalStats.totalSeconds);
            case "global_kills":
                return String.valueOf(globalStats.totalKills);
            case "global_waves":
                return String.valueOf(globalStats.totalWaves);
        }

        return null; // Not recognized by the Expansion
    }
}