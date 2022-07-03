package org.mobarena.stats.session;

import com.garbagemule.MobArena.framework.Arena;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Session {

    final UUID sessionId;
    final String arenaSlug;

    final SessionStats sessionStats;
    final Map<UUID, PlayerSessionStats> playerStats;

    public Session(UUID sessionId, String arenaSlug) {
        this.sessionId = sessionId;
        this.arenaSlug = arenaSlug;

        this.sessionStats = new SessionStats(sessionId, arenaSlug);
        this.playerStats = new HashMap<>();
    }

    public void playerJoin(Player player) {
        UUID playerId = player.getUniqueId();
        String playerName = player.getName();
        PlayerSessionStats stats = new PlayerSessionStats(sessionId, playerId, playerName);
        playerStats.put(playerId, stats);

        stats.joinTime = Instant.now();
    }

    public void playerReady(Player player, String className) {
        UUID playerId = player.getUniqueId();
        PlayerSessionStats stats = playerStats.get(playerId);
        if (stats == null) {
            return;
        }

        stats.readyTime = Instant.now();
        stats.className = className;
    }

    public void playerLeave(Arena arena, Player player) {
        UUID playerId = player.getUniqueId();
        if (sessionStats.startTime == null) {
            playerStats.remove(playerId);
            return;
        }

        PlayerSessionStats stats = playerStats.get(playerId);
        if (stats == null) {
            return;
        }

        stats.leaveTime = Instant.now();

        if (stats.conclusion == null) {
            stats.conclusion = PlayerConclusion.RETREAT;
        }

        StatsUtil.copy(arena, player, stats);
    }

    public void playerDeath(Arena arena, Player player) {
        UUID playerId = player.getUniqueId();
        if (sessionStats.startTime == null) {
            playerStats.remove(playerId);
            return;
        }

        PlayerSessionStats stats = playerStats.get(playerId);
        if (stats == null) {
            return;
        }

        stats.deathTime = Instant.now();

        if (stats.conclusion == null) {
            stats.conclusion = PlayerConclusion.DEFEAT;
        }

        StatsUtil.copy(arena, player, stats);
    }

    public void start() {
        sessionStats.startTime = Instant.now();
    }

    public void wave(int wave) {
        sessionStats.lastWave = wave;
    }

    public void complete() {
        sessionStats.conclusion = SessionConclusion.VICTORY;

        for (PlayerSessionStats playerStats : playerStats.values()) {
            if (playerStats.conclusion == null) {
                playerStats.conclusion = PlayerConclusion.VICTORY;
            }
        }
    }

    public void end() {
        sessionStats.endTime = Instant.now();

        if (sessionStats.conclusion == null) {
            sessionStats.conclusion = SessionConclusion.DEFEAT;
        }
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public String getArenaSlug() {
        return arenaSlug;
    }

    public SessionStats getSessionStats() {
        return sessionStats;
    }

    public Collection<PlayerSessionStats> getPlayerStats() {
        return playerStats.values();
    }

    public PlayerSessionStats getPlayerStats(UUID playerId) {
        return playerStats.get(playerId);
    }

    public void setPlayerStats(UUID playerId, PlayerSessionStats stats) {
        playerStats.put(playerId, stats);
    }

}
