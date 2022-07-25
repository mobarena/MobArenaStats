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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mobarena.stats.store.StatsStore;
import org.bukkit.entity.Player;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionListenerTest {

    SessionStore sessionStore;
    StatsStore statsStore;
    Executor asyncExecutor;
    Logger log;
    SessionListener subject;

    @BeforeEach
    void setup() {
        sessionStore = mock(SessionStore.class);
        statsStore = mock(StatsStore.class);
        asyncExecutor = Runnable::run;
        log = mock(Logger.class);
        subject = new SessionListener(
            sessionStore,
            statsStore,
            asyncExecutor,
            log
        );
    }

    @Test
    void freshJoinCreatesNewSessionAndCallsJoin() {
        Player player = mock(Player.class);
        Arena arena = mock(Arena.class);
        Session session = mock(Session.class);
        when(sessionStore.getByArena(arena)).thenReturn(null);
        when(sessionStore.create(arena)).thenReturn(session);
        ArenaPlayerJoinEvent event = new ArenaPlayerJoinEvent(player, arena);

        subject.on(event);

        verify(session).playerJoin(player);
    }

    @Test
    void nextJoinCallsPlayerJoinOnExistingSession() {
        Player player = mock(Player.class);
        Arena arena = mock(Arena.class);
        Session session = mock(Session.class);
        when(sessionStore.getByArena(arena)).thenReturn(session);
        ArenaPlayerJoinEvent event = new ArenaPlayerJoinEvent(player, arena);

        subject.on(event);

        verify(sessionStore, never()).create(arena);
        verify(session).playerJoin(player);
    }

    @Test
    void logsWarningIfPlayerReadyInNonExistentSession() {
        Player player = mock(Player.class);
        Arena arena = mock(Arena.class);
        when(sessionStore.getByArena(arena)).thenReturn(null);
        ArenaPlayerReadyEvent event = new ArenaPlayerReadyEvent(player, arena);

        subject.on(event);

        verify(log).warning(anyString());
    }

    @Test
    void callsPlayerReady() {
        String className = "knight";
        Player player = mock(Player.class);
        ArenaPlayer ap = mock(ArenaPlayer.class);
        ArenaClass ac = mock(ArenaClass.class);
        Arena arena = mock(Arena.class);
        Session session = mock(Session.class);
        when(arena.getArenaPlayer(player)).thenReturn(ap);
        when(ap.getArenaClass()).thenReturn(ac);
        when(ac.getSlug()).thenReturn(className);
        when(sessionStore.getByArena(arena)).thenReturn(session);
        ArenaPlayerReadyEvent event = new ArenaPlayerReadyEvent(player, arena);

        subject.on(event);

        verify(session).playerReady(player, className);
    }

    @Test
    void ignoresPlayerLeaveForSpectators() {
        Player player = mock(Player.class);
        Arena arena = mock(Arena.class);
        when(arena.inSpec(player)).thenReturn(true);
        ArenaPlayerLeaveEvent event = new ArenaPlayerLeaveEvent(player, arena);

        subject.on(event);

        verifyNoInteractions(sessionStore);
    }

    @Test
    void logsWarningIfPlayerLeavesInNonExistentSession() {
        Player player = mock(Player.class);
        Arena arena = mock(Arena.class);
        when(arena.inSpec(player)).thenReturn(false);
        when(sessionStore.getByArena(arena)).thenReturn(null);
        ArenaPlayerLeaveEvent event = new ArenaPlayerLeaveEvent(player, arena);

        subject.on(event);

        verify(log).warning(anyString());
    }

    @Test
    void callsPlayerLeaveInLobby() {
        Player player = mock(Player.class);
        Arena arena = mock(Arena.class);
        Session session = mock(Session.class);
        when(arena.inSpec(player)).thenReturn(false);
        when(arena.isRunning()).thenReturn(false);
        when(arena.getPlayersInLobby()).thenReturn(Collections.singleton(player));
        when(sessionStore.getByArena(arena)).thenReturn(session);
        ArenaPlayerLeaveEvent event = new ArenaPlayerLeaveEvent(player, arena);

        subject.on(event);

        verify(session).playerLeave(arena, player);
    }

    @Test
    void deletesSessionIfLastPlayerInLobby() {
        Player player = mock(Player.class);
        Arena arena = mock(Arena.class);
        Session session = mock(Session.class);
        when(arena.inSpec(player)).thenReturn(false);
        when(arena.isRunning()).thenReturn(false);
        when(arena.getPlayersInLobby()).thenReturn(Collections.singleton(player));
        when(sessionStore.getByArena(arena)).thenReturn(session);
        ArenaPlayerLeaveEvent event = new ArenaPlayerLeaveEvent(player, arena);

        subject.on(event);

        verify(sessionStore).delete(session);
    }

    @Test
    void doesNotDeleteSessionIfMorePlayersInLobby() {
        Player player = mock(Player.class);
        Player other = mock(Player.class);
        Arena arena = mock(Arena.class);
        Session session = mock(Session.class);
        when(arena.inSpec(player)).thenReturn(false);
        when(arena.isRunning()).thenReturn(false);
        when(arena.getPlayersInLobby()).thenReturn(new HashSet<>(Arrays.asList(player, other)));
        when(sessionStore.getByArena(arena)).thenReturn(session);
        ArenaPlayerLeaveEvent event = new ArenaPlayerLeaveEvent(player, arena);

        subject.on(event);

        verify(sessionStore, never()).delete(session);
    }

    @Test
    void callsPlayerLeaveInArenaButDoesNotDeleteSession() {
        Player player = mock(Player.class);
        Arena arena = mock(Arena.class);
        Session session = mock(Session.class);
        when(arena.inSpec(player)).thenReturn(false);
        when(arena.isRunning()).thenReturn(true);
        when(sessionStore.getByArena(arena)).thenReturn(session);
        ArenaPlayerLeaveEvent event = new ArenaPlayerLeaveEvent(player, arena);

        subject.on(event);

        verify(session).playerLeave(arena, player);
        verify(sessionStore, never()).delete(session);
    }

    @Test
    void logsWarningIfPlayerDiesInNonExistentSession() {
        Player player = mock(Player.class);
        Arena arena = mock(Arena.class);
        when(sessionStore.getByArena(arena)).thenReturn(null);
        ArenaPlayerDeathEvent event = new ArenaPlayerDeathEvent(player, arena, true);

        subject.on(event);

        verify(log).warning(anyString());
    }

    @Test
    void callsPlayerDeath() {
        Player player = mock(Player.class);
        Arena arena = mock(Arena.class);
        Session session = mock(Session.class);
        when(sessionStore.getByArena(arena)).thenReturn(session);
        ArenaPlayerDeathEvent event = new ArenaPlayerDeathEvent(player, arena, true);

        subject.on(event);

        verify(session).playerDeath(arena, player);
    }

    @Test
    void logsWarningIfArenaStartsWithoutSession() {
        Arena arena = mock(Arena.class);
        when(sessionStore.getByArena(arena)).thenReturn(null);
        ArenaStartEvent event = new ArenaStartEvent(arena);

        subject.on(event);

        verify(log).warning(anyString());
    }

    @Test
    void callsStart() {
        Arena arena = mock(Arena.class);
        Session session = mock(Session.class);
        when(sessionStore.getByArena(arena)).thenReturn(session);
        ArenaStartEvent event = new ArenaStartEvent(arena);

        subject.on(event);

        verify(session).start();
    }

    @Test
    void logsWarningIfWaveSpawnsWithoutSession() {
        Arena arena = mock(Arena.class);
        int wave = 3;
        when(sessionStore.getByArena(arena)).thenReturn(null);
        NewWaveEvent event = new NewWaveEvent(arena, null, wave);

        subject.on(event);

        verify(log).warning(anyString());
    }

    @Test
    void callsWave() {
        Arena arena = mock(Arena.class);
        int wave = 3;
        Session session = mock(Session.class);
        when(sessionStore.getByArena(arena)).thenReturn(session);
        NewWaveEvent event = new NewWaveEvent(arena, null, wave);

        subject.on(event);

        verify(session).wave(wave);
    }

    @Test
    void logsWarningIfArenaCompletesWithoutSession() {
        Arena arena = mock(Arena.class);
        when(sessionStore.getByArena(arena)).thenReturn(null);
        ArenaCompleteEvent event = new ArenaCompleteEvent(arena);

        subject.on(event);

        verify(log).warning(anyString());
    }

    @Test
    void callsComplete() {
        Arena arena = mock(Arena.class);
        Session session = mock(Session.class);
        when(sessionStore.getByArena(arena)).thenReturn(session);
        ArenaCompleteEvent event = new ArenaCompleteEvent(arena);

        subject.on(event);

        verify(session).complete();
    }

    @Test
    void logsWarningIfArenaEndsWithoutSession() {
        Arena arena = mock(Arena.class);
        when(sessionStore.getByArena(arena)).thenReturn(null);
        ArenaEndEvent event = new ArenaEndEvent(arena);

        subject.on(event);

        verify(log).warning(anyString());
    }

    @Test
    void callsEndAndDeletesSession() {
        Arena arena = mock(Arena.class);
        Session session = mock(Session.class);
        when(sessionStore.getByArena(arena)).thenReturn(session);
        ArenaEndEvent event = new ArenaEndEvent(arena);

        subject.on(event);

        verify(session).end();
        verify(sessionStore).delete(session);
    }

    @Test
    void doesNotSaveSessionIfNeverStarted() throws IOException {
        Arena arena = mock(Arena.class);
        Session session = mock(Session.class);
        when(arena.isRunning()).thenReturn(false);
        when(sessionStore.getByArena(arena)).thenReturn(session);
        ArenaEndEvent event = new ArenaEndEvent(arena);

        subject.on(event);

        verify(statsStore, never()).save(session);
    }

    @Test
    void logsInfoIfSessionSaveSucceeds() {
        Arena arena = mock(Arena.class);
        Session session = mock(Session.class);
        when(arena.isRunning()).thenReturn(true);
        when(sessionStore.getByArena(arena)).thenReturn(session);
        ArenaEndEvent event = new ArenaEndEvent(arena);

        subject.on(event);

        verify(log).info(anyString());
    }

    @Test
    void logsErrorIfSessionSaveThrows() throws IOException {
        Arena arena = mock(Arena.class);
        Session session = mock(Session.class);
        when(arena.isRunning()).thenReturn(true);
        when(sessionStore.getByArena(arena)).thenReturn(session);
        doThrow(new IOException()).when(statsStore).save(session);
        ArenaEndEvent event = new ArenaEndEvent(arena);

        subject.on(event);

        verify(log).log(eq(Level.SEVERE), anyString(), any(IOException.class));
    }

}
