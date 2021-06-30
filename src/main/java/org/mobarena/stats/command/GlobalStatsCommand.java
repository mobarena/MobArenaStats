package org.mobarena.stats.command;

import com.garbagemule.MobArena.commands.Command;
import com.garbagemule.MobArena.commands.CommandInfo;
import com.garbagemule.MobArena.framework.ArenaMaster;
import org.bukkit.command.CommandSender;
import org.mobarena.stats.MobArenaStats;
import org.mobarena.stats.MobArenaStatsPlugin;
import org.mobarena.stats.store.GlobalStats;
import org.mobarena.stats.store.StatsStore;

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

        plugin.getAsyncExecutor().execute(() -> {
            StatsStore store = plugin.getStatsStore();
            GlobalStats stats = store.getGlobalStats();
            sender.sendMessage("Global stats:");
            sender.sendMessage("- Total sessions: " + stats.totalSessions);
            sender.sendMessage("- Total duration: " + stats.totalSeconds + " secs");
            sender.sendMessage("- Total kills: " + stats.totalKills);
            sender.sendMessage("- Total waves: " + stats.totalWaves);
        });

        return true;
    }

}
