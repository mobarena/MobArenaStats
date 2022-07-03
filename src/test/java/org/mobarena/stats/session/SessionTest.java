package org.mobarena.stats.session;

import com.garbagemule.MobArena.ArenaPlayer;
import com.garbagemule.MobArena.framework.Arena;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionTest {

    Session subject;

    @BeforeEach
    void setup() {
        UUID sessionId = UUID.fromString("cafebabe-ea75-dead-beef-deadcafebabe");
        String arenaSlug = "castle";
        subject = new Session(
            sessionId,
            arenaSlug
        );
    }

    @Test
    void emptySessionHasNoPlayerStats() {
        assertThat(subject.getPlayerStats().size(), equalTo(0));
    }

    @Test
    void initPlayerStatsOnJoin() {
        UUID playerId = UUID.fromString("cafebabe-ea75-dead-beef-deadcafebabe");
        String playerName = "garbagemule";
        Player player = Mocks.player(playerId, playerName);

        subject.playerJoin(player);

        PlayerSessionStats actual = subject.getPlayerStats(playerId);
        assertThat(actual.playerId, equalTo(playerId));
        assertThat(actual.playerName, equalTo(playerName));
        assertThat(actual.className, nullValue());
        assertThat(actual.readyTime, nullValue());
        assertThat(actual.leaveTime, nullValue());
        assertThat(actual.deathTime, nullValue());
        assertThat(actual.kills, equalTo(0));
        assertThat(actual.dmgDone, equalTo(0));
        assertThat(actual.dmgTaken, equalTo(0));
        assertThat(actual.swings, equalTo(0));
        assertThat(actual.hits, equalTo(0));
        assertThat(actual.lastWave, equalTo(0));
        assertThat(actual.conclusion, nullValue());
    }

    @Test
    void setJoinTimeOnJoin() {
        UUID playerId = UUID.fromString("cafebabe-ea75-dead-beef-deadcafebabe");
        Player player = Mocks.player(playerId, "garbagemule");

        subject.playerJoin(player);

        PlayerSessionStats actual = subject.getPlayerStats(playerId);
        assertThat(actual.joinTime, notNullValue());
    }

    @Test
    void setReadyTimeOnReady() {
        UUID playerId = UUID.fromString("cafebabe-ea75-dead-beef-deadcafebabe");
        Player player = Mocks.player(playerId, "garbagemule");
        String className = "knight";
        subject.playerJoin(player);

        subject.playerReady(player, className);

        PlayerSessionStats actual = subject.getPlayerStats(playerId);
        assertThat(actual.readyTime, notNullValue());
    }

    @Test
    void setClassNameOnReady() {
        UUID playerId = UUID.fromString("cafebabe-ea75-dead-beef-deadcafebabe");
        Player player = Mocks.player(playerId, "garbagemule");
        String className = "knight";
        subject.playerJoin(player);

        subject.playerReady(player, className);

        PlayerSessionStats actual = subject.getPlayerStats(playerId);
        assertThat(actual.className, equalTo(className));
    }

    @Test
    void removeFromSessionOnLeaveBeforeStart() {
        UUID playerId = UUID.fromString("cafebabe-ea75-dead-beef-deadcafebabe");
        Player player = Mocks.player(playerId, "garbagemule");
        Arena arena = mock(Arena.class);
        subject.playerJoin(player);

        subject.playerLeave(arena, player);

        PlayerSessionStats actual = subject.getPlayerStats(playerId);
        assertThat(actual, nullValue());
    }

    @Test
    void setLeaveTimeOnLeave() {
        UUID playerId = UUID.fromString("cafebabe-ea75-dead-beef-deadcafebabe");
        Player player = Mocks.player(playerId, "garbagemule");
        Arena arena = mock(Arena.class);
        ArenaPlayer ap = mock(ArenaPlayer.class);
        when(arena.getArenaPlayer(player)).thenReturn(ap);
        subject.playerJoin(player);
        subject.start();

        subject.playerLeave(arena, player);

        PlayerSessionStats actual = subject.getPlayerStats(playerId);
        assertThat(actual.leaveTime, notNullValue());
    }

    @Test
    void setRetreatOnLeaveIfNoOtherConclusion() {
        UUID playerId = UUID.fromString("cafebabe-ea75-dead-beef-deadcafebabe");
        Player player = Mocks.player(playerId, "garbagemule");
        Arena arena = mock(Arena.class);
        ArenaPlayer ap = mock(ArenaPlayer.class);
        when(arena.getArenaPlayer(player)).thenReturn(ap);
        subject.playerJoin(player);
        subject.start();

        subject.playerLeave(arena, player);

        PlayerSessionStats actual = subject.getPlayerStats(playerId);
        assertThat(actual.conclusion, equalTo(PlayerConclusion.RETREAT));
    }

    @Test
    void dontOverwriteConclusionOnLeave() {
        UUID playerId = UUID.fromString("cafebabe-ea75-dead-beef-deadcafebabe");
        Player player = Mocks.player(playerId, "garbagemule");
        Arena arena = mock(Arena.class);
        ArenaPlayer ap = mock(ArenaPlayer.class);
        when(arena.getArenaPlayer(player)).thenReturn(ap);
        subject.playerJoin(player);
        subject.start();
        subject.getPlayerStats(playerId).conclusion = PlayerConclusion.VICTORY;

        subject.playerLeave(arena, player);

        PlayerSessionStats actual = subject.getPlayerStats(playerId);
        assertThat(actual.conclusion, not(equalTo(PlayerConclusion.RETREAT)));
    }

    @Test
    void removeFromSessionOnDeathBeforeStart() {
        UUID playerId = UUID.fromString("cafebabe-ea75-dead-beef-deadcafebabe");
        Player player = Mocks.player(playerId, "garbagemule");
        Arena arena = mock(Arena.class);
        subject.playerJoin(player);

        subject.playerDeath(arena, player);

        PlayerSessionStats actual = subject.getPlayerStats(playerId);
        assertThat(actual, nullValue());
    }

    @Test
    void setDeathTimeOnDeath() {
        UUID playerId = UUID.fromString("cafebabe-ea75-dead-beef-deadcafebabe");
        Player player = Mocks.player(playerId, "garbagemule");
        Arena arena = mock(Arena.class);
        ArenaPlayer ap = mock(ArenaPlayer.class);
        when(arena.getArenaPlayer(player)).thenReturn(ap);
        subject.playerJoin(player);
        subject.start();

        subject.playerDeath(arena, player);

        PlayerSessionStats actual = subject.getPlayerStats(playerId);
        assertThat(actual.deathTime, notNullValue());
    }

    @Test
    void setDefeatOnDeathIfNoOtherConclusion() {
        UUID playerId = UUID.fromString("cafebabe-ea75-dead-beef-deadcafebabe");
        Player player = Mocks.player(playerId, "garbagemule");
        Arena arena = mock(Arena.class);
        ArenaPlayer ap = mock(ArenaPlayer.class);
        when(arena.getArenaPlayer(player)).thenReturn(ap);
        subject.playerJoin(player);
        subject.start();

        subject.playerDeath(arena, player);

        PlayerSessionStats actual = subject.getPlayerStats(playerId);
        assertThat(actual.conclusion, equalTo(PlayerConclusion.DEFEAT));
    }

    @Test
    void dontOverwriteConclusionOnDeath() {
        UUID playerId = UUID.fromString("cafebabe-ea75-dead-beef-deadcafebabe");
        Player player = Mocks.player(playerId, "garbagemule");
        Arena arena = mock(Arena.class);
        ArenaPlayer ap = mock(ArenaPlayer.class);
        when(arena.getArenaPlayer(player)).thenReturn(ap);
        subject.playerJoin(player);
        subject.start();
        subject.getPlayerStats(playerId).conclusion = PlayerConclusion.VICTORY;

        subject.playerDeath(arena, player);

        PlayerSessionStats actual = subject.getPlayerStats(playerId);
        assertThat(actual.conclusion, not(equalTo(PlayerConclusion.DEFEAT)));
    }

    @Test
    void initSessionStatsOnCreate() {
        SessionStats actual = subject.getSessionStats();
        assertThat(actual.sessionId, notNullValue());
        assertThat(actual.startTime, nullValue());
        assertThat(actual.endTime, nullValue());
        assertThat(actual.lastWave, equalTo(0));
        assertThat(actual.conclusion, nullValue());
    }

    @Test
    void setStartTimeOnStart() {
        subject.start();

        SessionStats actual = subject.getSessionStats();
        assertThat(actual.startTime, notNullValue());
    }

    @Test
    void setLastWaveTimeOnWave() {
        int wave = 3;

        subject.wave(wave);

        SessionStats actual = subject.getSessionStats();
        assertThat(actual.lastWave, equalTo(wave));
    }

    @Test
    void setVictoryOnComplete() {
        subject.complete();

        SessionStats actual = subject.getSessionStats();
        assertThat(actual.conclusion, equalTo(SessionConclusion.VICTORY));
    }

    @Test
    void setSurvivorVictoryOnComplete() {
        UUID playerId = UUID.fromString("ca11ab1e-cafe-babe-ea75-babecafebeef");
        Player player = Mocks.player(playerId, "garbagemule");
        subject.playerJoin(player);

        subject.complete();

        PlayerSessionStats actual = subject.getPlayerStats(playerId);
        assertThat(actual.conclusion, equalTo(PlayerConclusion.VICTORY));
    }

    @Test
    void dontOverwriteCorpseConclusionOnComplete() {
        UUID corpseId = UUID.fromString("deadbeef-dead-dead-dead-deadcafebeef");
        Player corpse = Mocks.player(corpseId, "trashdonkey");
        UUID survivorId = UUID.fromString("ca11ab1e-cafe-babe-ea75-babecafebeef");
        Player survivor = Mocks.player(survivorId, "garbagemule");
        Arena arena = mock(Arena.class);
        ArenaPlayer ap = mock(ArenaPlayer.class);
        when(arena.getArenaPlayer(corpse)).thenReturn(ap);
        subject.playerJoin(corpse);
        subject.playerJoin(survivor);
        subject.start();
        subject.playerDeath(arena, corpse);

        subject.complete();

        PlayerSessionStats actual = subject.getPlayerStats(corpseId);
        assertThat(actual.conclusion, equalTo(PlayerConclusion.DEFEAT));
    }

    @Test
    void setEndTimeTimeOnEnd() {
        subject.end();

        SessionStats actual = subject.getSessionStats();
        assertThat(actual.endTime, notNullValue());
    }

    @Test
    void setDefeatOnEnd() {
        subject.end();

        SessionStats actual = subject.getSessionStats();
        assertThat(actual.conclusion, equalTo(SessionConclusion.DEFEAT));
    }

    @Test
    void dontOverwriteConclusionOnEnd() {
        subject.complete();

        subject.end();

        SessionStats actual = subject.getSessionStats();
        assertThat(actual.conclusion, not(equalTo(SessionConclusion.DEFEAT)));
    }

}
