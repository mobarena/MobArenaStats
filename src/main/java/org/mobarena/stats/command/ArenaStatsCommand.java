package org.mobarena.stats.command;

import com.garbagemule.MobArena.commands.Command;
import com.garbagemule.MobArena.commands.CommandInfo;
import com.garbagemule.MobArena.framework.ArenaMaster;
import com.garbagemule.MobArena.util.Slugs;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mobarena.stats.MobArenaStats;
import org.mobarena.stats.MobArenaStatsPlugin;
import org.mobarena.stats.store.ArenaStats;
import org.mobarena.stats.store.StatsStore;

import java.util.List;

@CommandInfo(
    name = "arena-stats",
    pattern = "arena-stats",
    usage = "/ma arena-stats <arena>",
    desc = "show overall stats for the given arena",
    permission = "mobarenastats.command.arena-stats"
)
public class ArenaStatsCommand implements Command {

    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        // :(
        MobArenaStats plugin = MobArenaStatsPlugin.getInstance();

        if (args.length < 1) {
            return false;
        }
        String slug = Slugs.create(args[0]);

        plugin.getAsyncExecutor().execute(() -> {
            StatsStore store = plugin.getStatsStore();
            ArenaStats stats = store.getArenaStats(slug);
            sender.sendMessage("Stats for arena " + slug + ":");
            sender.sendMessage("- Highest wave: " + stats.highestWave);
            sender.sendMessage("- Longest duration: " + stats.highestSeconds + " secs");
            sender.sendMessage("- Most kills: " + stats.highestKills);
            sender.sendMessage("- Total sessions: " + stats.totalSessions);
            sender.sendMessage("- Total duration: " + stats.totalSeconds + " secs");
            sender.sendMessage("- Total kills: " + stats.totalKills);
            sender.sendMessage("- Total waves: " + stats.totalWaves);
        });

        return true;
    }

    @Override
    public List<String> tab(ArenaMaster am, Player player, String... args) {
        // TODO: tab complete arena slugs?
        return Command.super.tab(am, player, args);
    }

}
