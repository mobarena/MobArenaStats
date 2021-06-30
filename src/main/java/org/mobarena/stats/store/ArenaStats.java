package org.mobarena.stats.store;

public class ArenaStats {

    public final int highestWave;
    public final int highestSeconds;
    public final int highestKills;
    public final int totalSessions;
    public final long totalSeconds;
    public final long totalKills;
    public final long totalWaves;

    public ArenaStats(
        int highestWave,
        int highestSeconds,
        int highestKills,
        int totalSessions,
        long totalSeconds,
        long totalKills,
        long totalWaves
    ) {
        this.highestWave = highestWave;
        this.highestSeconds = highestSeconds;
        this.highestKills = highestKills;
        this.totalSessions = totalSessions;
        this.totalSeconds = totalSeconds;
        this.totalKills = totalKills;
        this.totalWaves = totalWaves;
    }

}
