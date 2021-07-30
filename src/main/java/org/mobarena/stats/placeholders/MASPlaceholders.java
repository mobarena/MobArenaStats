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

        ArenaStats arenaStats = store.getArenaStats(arenaName);
        PlayerStats playerStats = store.getPlayerStats(playerName);

        long totalPlayerMilliseconds = playerStats.totalSeconds * 1000;
        long totalArenaMilliseconds = arenaStats.totalSeconds * 1000;
        long highestArenaMilliseconds = arenaStats.totalSeconds * 1000;

        if (identifier.endsWith("_total-sessions-test")) {
            String testPlayer = identifier.replace("_total-sessions-test", "");
            PlayerStats playerStatsTest = store.getPlayerStats(testPlayer);
            return Integer.toString(playerStatsTest.totalSessions) + " " + testPlayer;
        }

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
            if ("highest-wave".equals(param)) {
                return Integer.toString(arenaStats.highestWave);
            } else if ("highest-kills".equals(param)) {
                return Integer.toString(arenaStats.highestKills);
            } else if ("highest-seconds".equals(param)) {
                return Integer.toString(arenaStats.highestSeconds);
            } else if ("highest-seconds-formatted".equals(param)) {
                return DurationFormatUtils.formatDuration(highestArenaMilliseconds, "HH:mm:ss", true);
            } else if ("total-kills".equals(param)) {
                return Long.toString(arenaStats.totalKills);
            } else if ("total-waves".equals(param)) {
                return Long.toString(arenaStats.totalWaves);
            } else if ("total-sessions".equals(param)) {
                return Integer.toString(arenaStats.totalSessions);
            } else if ("total-seconds".equals(param)) {
                return Long.toString(arenaStats.totalSeconds);
            } else if ("total-seconds-formatted".equals(param)) {
                return DurationFormatUtils.formatDuration(totalArenaMilliseconds, "HH:mm:ss", true);
            }
        }
        // player_stat_playerName
        if (args.length == 3) {
            final String param = args[2];

            if ("total-sessions".equals(param)) {
                return Integer.toString(playerStats.totalSessions);
            } else if ("total-kills".equals(param)) {
                return Long.toString(playerStats.totalKills);
            } else if ("total-seconds".equals(param)) {
                return Long.toString(playerStats.totalSeconds);
            } else if ("total-seconds-formatted".equals(param)) {
                return DurationFormatUtils.formatDuration(totalPlayerMilliseconds, "HH:mm:ss", true);
            } else if ("total-waves".equals(param)) {
                return Long.toString(playerStats.totalWaves);
            }
        }
        return null; // Not recognized by the Expansion
}
}