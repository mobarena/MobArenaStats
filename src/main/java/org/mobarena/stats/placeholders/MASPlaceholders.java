package org.mobarena.stats.placeholders;

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

        long totalPlayerMilliseconds = name.totalSeconds * 1000;
        long totalArenaMilliseconds = arena.totalSeconds * 1000;
        long highestArenaMilliseconds = arena.totalSeconds * 1000;


        switch (identifier) {
            case "global_sessions":
                return Integer.toString(globalStats.totalSessions);
            case "global_seconds":
                return Long.toString(globalStats.totalSeconds);
            case "global_seconds-formatted":
                long globalDurationFormatted = globalStats.totalSeconds * 1000;
                return DurationFormatUtils.formatDuration(globalDurationFormatted, "HH:mm:ss", true);
            case "global_kills":
                return Long.toString(globalStats.totalKills);
            case "global_waves":
                return Long.toString(globalStats.totalWaves);
        }
        // arenaName_stat
        if (args.length == 2) {
            final String param = args[1];
            switch (param) {
                case "highest-wave":
                    return Integer.toString(arena.highestWave);

                case "highest-kills":
                    return Integer.toString(arena.highestKills);

                case "highest-seconds":
                    return Integer.toString(arena.highestSeconds);

                case "highest-seconds-formatted":
                    return DurationFormatUtils.formatDuration(highestArenaMilliseconds, "HH:mm:ss", true);

                case "total-kills":
                    return Long.toString(arena.totalKills);

                case "total-waves":
                    return Long.toString(arena.totalWaves);

                case "total-sessions":
                    return Integer.toString(arena.totalSessions);

                case "total-seconds":
                    return Long.toString(arena.totalSeconds);

                case "total-seconds-formatted":
                    return DurationFormatUtils.formatDuration(totalArenaMilliseconds, "HH:mm:ss", true);
            }
        }
        // player_playerName_stat
        if (args.length == 3) {
            final String param = args[2];

            switch (param) {

                case "total-sessions":
                    return Integer.toString(name.totalSessions);

                case "total-kills":
                    return Long.toString(name.totalKills);

                case "total-seconds":
                    return Long.toString(name.totalSeconds);

                case "total-seconds-formatted":
                    return DurationFormatUtils.formatDuration(totalPlayerMilliseconds, "HH:mm:ss", true);

                case "total-waves":
                    return Long.toString(name.totalWaves);

            }
        }
        return null; // Not recognized by the Expansion
}
}