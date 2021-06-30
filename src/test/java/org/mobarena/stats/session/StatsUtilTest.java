package org.mobarena.stats.session;

import com.garbagemule.MobArena.ArenaPlayer;
import com.garbagemule.MobArena.ArenaPlayerStatistics;
import com.garbagemule.MobArena.framework.Arena;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatsUtilTest {

    @Test
    void copiesStatsFromMobArenaObject() {
        UUID sessionId = UUID.fromString("cafebabe-ea75-dead-beef-deadcafebabe");
        UUID playerId = UUID.fromString("babecafe-dead-beef-ea75-deadbeefbeef");
        String playerName = "garbagemule";
        int kills = 18;
        int dmgDone = 1587;
        int dmgTaken = 7159;
        int swings = 1457;
        int hits = 1337;
        int lastWave = 11;
        Player player = mock(Player.class);
        Arena arena = mock(Arena.class);
        ArenaPlayer ap = mock(ArenaPlayer.class);
        ArenaPlayerStatistics aps = mock(ArenaPlayerStatistics.class);
        when(arena.getArenaPlayer(player)).thenReturn(ap);
        when(ap.getStats()).thenReturn(aps);
        when(aps.getInt("kills")).thenReturn(kills);
        when(aps.getInt("dmgDone")).thenReturn(dmgDone);
        when(aps.getInt("dmgTaken")).thenReturn(dmgTaken);
        when(aps.getInt("swings")).thenReturn(swings);
        when(aps.getInt("hits")).thenReturn(hits);
        when(aps.getInt("lastWave")).thenReturn(lastWave);
        PlayerSessionStats target = new PlayerSessionStats(sessionId, playerId, playerName);

        StatsUtil.copy(arena, player, target);

        assertThat(target.kills, equalTo(kills));
        assertThat(target.dmgDone, equalTo(dmgDone));
        assertThat(target.dmgTaken, equalTo(dmgTaken));
        assertThat(target.swings, equalTo(swings));
        assertThat(target.hits, equalTo(hits));
        assertThat(target.lastWave, equalTo(lastWave));
    }

}
