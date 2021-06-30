package org.mobarena.stats.store;

import org.mobarena.stats.session.Session;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CachingStatsStore implements StatsStore {

    private final StatsStore delegate;

    private GlobalStats globalStats;
    private final Map<String, ArenaStats> arenaStats;
    private final Map<String, PlayerStats> playerStats;

    public CachingStatsStore(StatsStore delegate) {
        this.delegate = delegate;

        this.globalStats = null;
        this.arenaStats = new HashMap<>();
        this.playerStats = new HashMap<>();
    }

    @Override
    public void save(Session session) throws IOException {
        delegate.save(session);

        globalStats = null;
        arenaStats.remove(session.getArenaSlug());
        session.getPlayerStats().forEach(stats -> playerStats.remove(stats.playerName));
    }

    @Override
    public void delete(UUID sessionId) {
        delegate.delete(sessionId);

        globalStats = null;
        arenaStats.clear();
        playerStats.clear();
    }

    @Override
    public GlobalStats getGlobalStats() {
        if (globalStats == null) {
            globalStats = delegate.getGlobalStats();
        }
        return globalStats;
    }

    @Override
    public ArenaStats getArenaStats(String slug) {
        return arenaStats.computeIfAbsent(slug, delegate::getArenaStats);
    }

    @Override
    public PlayerStats getPlayerStats(String name) {
        return playerStats.computeIfAbsent(name, delegate::getPlayerStats);
    }

    @Override
    public void export(StatsStore target) throws IOException {
        delegate.export(target);
    }

}
