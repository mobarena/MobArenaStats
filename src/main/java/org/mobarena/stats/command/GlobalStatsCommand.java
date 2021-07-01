package org.mobarena.stats.command;

import com.garbagemule.MobArena.Messenger;
import com.garbagemule.MobArena.commands.Command;
import com.garbagemule.MobArena.commands.CommandInfo;
import com.garbagemule.MobArena.framework.ArenaMaster;
import org.bukkit.command.CommandSender;
import org.mobarena.stats.MobArenaStats;
import org.mobarena.stats.MobArenaStatsPlugin;
import org.mobarena.stats.store.GlobalStats;
import org.mobarena.stats.store.StatsStore;

import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;
import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.RESET;
import static org.bukkit.ChatColor.YELLOW;

@CommandInfo(
    name = "global-stats",
    pattern = "global-stats",
    usage = "/ma global-stats",
    desc = "show stats across all sessions",
    permission = "mobarenastats.command.global-stats"
)
public class GlobalStatsCommand implements Command {

    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        // :(
        MobArenaStats plugin = MobArenaStatsPlugin.getInstance();

        Messenger messenger = am.getGlobalMessenger();
        plugin.getAsyncExecutor().execute(() -> {
            StatsStore store = plugin.getStatsStore();
            GlobalStats stats = store.getGlobalStats();
            List<String> lines = Arrays.asList(
                format("Global stats across %sall arenas%s:", YELLOW, RESET),
                format("- Total sessions: %s%d%s", AQUA, stats.totalSessions, RESET),
                format("- Total duration: %s%d%s secs", AQUA, stats.totalSeconds, RESET),
                format("- Total kills: %s%d%s", AQUA, stats.totalKills, RESET),
                format("- Total waves: %s%d%s", AQUA, stats.totalWaves, RESET)
            );
            messenger.tell(sender, String.join("\n", lines));
        });

        return true;
    }

}
