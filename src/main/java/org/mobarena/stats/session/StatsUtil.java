package org.mobarena.stats.session;

import com.garbagemule.MobArena.ArenaPlayer;
import com.garbagemule.MobArena.ArenaPlayerStatistics;
import com.garbagemule.MobArena.framework.Arena;
import org.bukkit.entity.Player;

final class StatsUtil {

    private StatsUtil() {
        // OK BOSS
    }

    static void copy(Arena arena, Player player, PlayerSessionStats target) {
        ArenaPlayer ap = arena.getArenaPlayer(player);
        if (ap == null) {
            return;
        }

        ArenaPlayerStatistics aps = ap.getStats();
        if (aps == null) {
            return;
        }

        target.kills = aps.getInt("kills");
        target.dmgDone = aps.getInt("dmgDone");
        target.dmgTaken = aps.getInt("dmgTaken");
        target.swings = aps.getInt("swings");
        target.hits = aps.getInt("hits");
        target.lastWave = aps.getInt("lastWave");
    }

}
