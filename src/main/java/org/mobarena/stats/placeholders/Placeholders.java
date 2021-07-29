package org.mobarena.stats.placeholders;

import org.bukkit.OfflinePlayer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.mobarena.stats.MobArenaStatsPlugin;
import org.mobarena.stats.MobArenaStats;
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

    public String onRequest(OfflinePlayer player, String params) {
        if(params.equalsIgnoreCase("global_sessions")){
            return String.valueOf(globalStats.totalSessions);
        }


        return null; // Placeholder is unknown by the Expansion
    }
}