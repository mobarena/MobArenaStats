package org.mobarena.stats.store;

public class PlayerStats {

    public final int totalSessions;
    public final long totalSeconds;
    public final long totalKills;
    public final long totalWaves;

    public PlayerStats(
        int totalSessions,
        long totalSeconds,
        long totalKills,
        long totalWaves
    ) {
        this.totalSessions = totalSessions;
        this.totalSeconds = totalSeconds;
        this.totalKills = totalKills;
        this.totalWaves = totalWaves;
    }

}
