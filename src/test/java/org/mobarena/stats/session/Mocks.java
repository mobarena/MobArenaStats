package org.mobarena.stats.session;

import com.garbagemule.MobArena.framework.Arena;
import org.bukkit.entity.Player;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class Mocks {

    static Arena arena(String arenaSlug) {
        Arena arena = mock(Arena.class);
        when(arena.getSlug()).thenReturn(arenaSlug);
        return arena;
    }

    static Player player(UUID playerId, String playerName) {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(playerId);
        when(player.getName()).thenReturn(playerName);
        return player;
    }

}
