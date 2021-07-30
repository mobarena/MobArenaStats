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
import org.mobarena.stats.store.PlayerStats;
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
        final String playerName = args[1];
        ArenaStats arena = store.getArenaStats(arenaName);
        PlayerStats name = store.getPlayerStats(playerName);

        switch (identifier) {
            case "global_sessions":
                return Integer.toString(globalStats.totalSessions);
            case "global_seconds":
                return Long.toString(globalStats.totalSeconds);
            case "global_duration_formatted":
                return DurationFormatUtils.formatDuration(globalDurationFormatted, "H Hours, mm minutes, and ss seconds", true);
            case "global_kills":
                return Long.toString(globalStats.totalKills);
            case "global_waves":
                return Long.toString(globalStats.totalWaves);
        }
        // arenaName_stat
        if (args.length == 2){
            final String param = args[1];

            if ("highest-wave".equals(param)){
                return Integer.toString(arena.highestWave);
            }

            else if ("highest-kills".equals(param)){
                Integer.toString(arena.highestKills);
            }

            else if ("highest-seconds".equals(param)){
                Integer.toString(arena.highestSeconds);
            }
            else if ("highest-duration-formatted".equals(param)){
                int highestMilliseconds = (int) (name.totalSeconds * 1000);
                return DurationFormatUtils.formatDuration(highestMilliseconds, "H Hours, mm minutes, and ss seconds", true);
            }

            else if ("total-kills".equals(param)){
                Long.toString(arena.totalKills);
            }

            else if ("total-waves".equals(param)){
                Long.toString(arena.totalWaves);
            }

            else if ("total-sessions".equals(param)){
                Integer.toString(arena.totalSessions);
            }
            else if ("total-seconds".equals(param)) {
                return Long.toString(name.totalSeconds);
            }
            else if ("total-duration-formatted".equals(param)) {
                int totalMilliseconds = (int) (name.totalSeconds * 1000);
                return DurationFormatUtils.formatDuration(totalMilliseconds, "H Hours, mm minutes, and ss seconds", true);

            }
        }
        // player_playerName_stat
        if (args.length == 3) {
            final String param = args[2];

            if ("total-sessions".equals(param)) {
                return Integer.toString(name.totalSessions);
            }
            else if ("total-kills".equals(param)) {
                return Long.toString(name.totalKills);
            }
            else if ("total-duration".equals(param)) {
                return Long.toString(name.totalSeconds);
            }
            else if ("total-duration-formatted".equals(param)) {
                int totalMilliseconds = (int) (name.totalSeconds * 1000);
                return DurationFormatUtils.formatDuration(totalMilliseconds, "H Hours, mm minutes, and ss seconds", true);

            }
            else if ("total-waves".equals(param)) {
                return Long.toString(name.totalWaves);
            }
        }
        return null; // Not recognized by the Expansion
}
}