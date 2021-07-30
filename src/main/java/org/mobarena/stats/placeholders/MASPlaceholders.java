package org.mobarena.stats.placeholders;

import com.garbagemule.MobArena.framework.ArenaMaster;
import com.garbagemule.MobArena.util.Slugs;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.OfflinePlayer;
import org.mobarena.stats.MobArenaStats;
import org.mobarena.stats.MobArenaStatsPlugin;
import org.mobarena.stats.store.ArenaStats;
import org.mobarena.stats.store.GlobalStats;
import org.mobarena.stats.store.StatsStore;

public class MASPlaceholders extends PlaceholderExpansion {

    MobArenaStats plugin = MobArenaStatsPlugin.getInstance();
    StatsStore store = plugin.getStatsStore();
    GlobalStats globalStats = store.getGlobalStats();

    long globalDurationFormatted = globalStats.totalSeconds * 1000;


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

    @Override
    public String onRequest(OfflinePlayer player, String identifier) {

        final String[] args = identifier.split("\\_");
        final String arenaName = args[0];
        ArenaStats arena = store.getArenaStats(arenaName);

        switch (identifier) {
            case "global_sessions":
                return Integer.toString(globalStats.totalSessions);
            case "global_duration":
                return Long.toString(globalStats.totalSeconds);
            case "global_duration_formatted":
                return DurationFormatUtils.formatDuration(globalDurationFormatted, "H:mm:ss", true);
            case "global_kills":
                return Long.toString(globalStats.totalKills);
            case "global_waves":
                return Long.toString(globalStats.totalWaves);
        }
        // arenaName_stat
        if (args.length == 2){
            final String param = args[1];

            if ("highest_wave".equals(param)){
                return Integer.toString(arena.highestWave);
            }
        }
        return null; // Not recognized by the Expansion
    }
}