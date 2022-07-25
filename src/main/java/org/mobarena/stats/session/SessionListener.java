package org.mobarena.stats.session;

import com.garbagemule.MobArena.ArenaClass;
import com.garbagemule.MobArena.ArenaPlayer;
import com.garbagemule.MobArena.events.ArenaCompleteEvent;
import com.garbagemule.MobArena.events.ArenaEndEvent;
import com.garbagemule.MobArena.events.ArenaPlayerDeathEvent;
import com.garbagemule.MobArena.events.ArenaPlayerJoinEvent;
import com.garbagemule.MobArena.events.ArenaPlayerLeaveEvent;
import com.garbagemule.MobArena.events.ArenaPlayerReadyEvent;
import com.garbagemule.MobArena.events.ArenaStartEvent;
import com.garbagemule.MobArena.events.NewWaveEvent;
import com.garbagemule.MobArena.framework.Arena;
import org.mobarena.stats.store.StatsStore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SessionListener implements Listener {

    private final SessionStore sessionStore;
    private final StatsStore statsStore;
    private final Executor asyncExecutor;
    private final Logger log;

    public SessionListener(
        SessionStore sessionStore,
        StatsStore statsStore,
        Executor asyncExecutor,
        Logger log
    ) {
        this.sessionStore = sessionStore;
        this.statsStore = statsStore;
        this.asyncExecutor = asyncExecutor;
        this.log = log;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(ArenaPlayerJoinEvent event) {
        Arena arena = event.getArena();
        Player player = event.getPlayer();

        Session session = sessionStore.getByArena(arena);
        if (session == null) {
            session = sessionStore.create(arena);
        }

        session.playerJoin(player);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(ArenaPlayerReadyEvent event) {
        Arena arena = event.getArena();
        Player player = event.getPlayer();
        String className = getClassName(arena, player);

        Session session = sessionStore.getByArena(arena);
        if (session == null) {
            log.warning("Unexpected ready event for non-existent session of arena " + arena.getSlug());
            return;
        }

        session.playerReady(player, className);
    }

    private String getClassName(Arena arena, Player player) {
        ArenaPlayer ap = arena.getArenaPlayer(player);
        if (ap == null) {
            return null;
        }

        ArenaClass ac = ap.getArenaClass();
        if (ac == null) {
            return null;
        }

        return ac.getSlug();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(ArenaPlayerLeaveEvent event) {
        Arena arena = event.getArena();
        Player player = event.getPlayer();

        if (arena.inSpec(player)) {
            return;
        }

        Session session = sessionStore.getByArena(arena);
        if (session == null) {
            log.warning("Unexpected leave event for non-existent session of arena " + arena.getSlug());
            return;
        }

        session.playerLeave(arena, player);

        if (!arena.isRunning()) {
            if (arena.getPlayersInLobby().size() <= 1) {
                sessionStore.delete(session);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(ArenaPlayerDeathEvent event) {
        Arena arena = event.getArena();
        Player player = event.getPlayer();

        Session session = sessionStore.getByArena(arena);
        if (session == null) {
            log.warning("Unexpected death event for non-existent session of arena " + arena.getSlug());
            return;
        }

        session.playerDeath(arena, player);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(ArenaStartEvent event) {
        Arena arena = event.getArena();

        Session session = sessionStore.getByArena(arena);
        if (session == null) {
            log.warning("Unexpected start event for non-existent session of arena " + arena.getSlug());
            return;
        }

        session.start();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(NewWaveEvent event) {
        Arena arena = event.getArena();
        int wave = event.getWaveNumber();

        Session session = sessionStore.getByArena(arena);
        if (session == null) {
            log.warning("Unexpected wave event for non-existent session of arena " + arena.getSlug());
            return;
        }

        session.wave(wave);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(ArenaCompleteEvent event) {
        Arena arena = event.getArena();

        Session session = sessionStore.getByArena(arena);
        if (session == null) {
            log.warning("Unexpected complete event for non-existent session of arena " + arena.getSlug());
            return;
        }

        session.complete();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(ArenaEndEvent event) {
        Arena arena = event.getArena();

        Session session = sessionStore.getByArena(arena);
        if (session == null) {
            log.warning("Unexpected end event for non-existent session of arena " + arena.getSlug());
            return;
        }

        session.end();

        if (!arena.isRunning()) {
            // Session never started, so just clean up and bail
            sessionStore.delete(session);
            return;
        }

        CompletableFuture.runAsync(() -> {
            try {
                statsStore.save(session);
                sessionStore.delete(session);
                log.info("Session (" + session.sessionId + ") for arena " + session.arenaSlug + " saved.");
            } catch (Exception e) {
                log.log(Level.SEVERE, "Failed to save session (" + session.sessionId + ") for arena " + session.arenaSlug, e);
                sessionStore.delete(session);
            }
        }, asyncExecutor);
    }

}
