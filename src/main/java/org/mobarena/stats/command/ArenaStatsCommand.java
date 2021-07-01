package org.mobarena.stats.command;

import com.garbagemule.MobArena.Messenger;
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

import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;
import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.RESET;
import static org.bukkit.ChatColor.YELLOW;

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

        Messenger messenger = am.getGlobalMessenger();
        plugin.getAsyncExecutor().execute(() -> {
            StatsStore store = plugin.getStatsStore();
            ArenaStats stats = store.getArenaStats(slug);
            List<String> lines = Arrays.asList(
                format("Stats for arena %s%s%s:", YELLOW, slug, RESET),
                format("- Highest wave: %s%d%s", AQUA, stats.highestWave, RESET),
                format("- Longest duration: %s%d%s secs", AQUA, stats.highestSeconds, RESET),
                format("- Most kills: %s%d%s", AQUA, stats.highestKills, RESET),
                format("- Total sessions: %s%d%s", AQUA, stats.totalSessions, RESET),
                format("- Total duration: %s%d%s secs", AQUA, stats.totalSeconds, RESET),
                format("- Total kills: %s%d%s", AQUA, stats.totalKills, RESET),
                format("- Total waves: %s%d%s", AQUA, stats.totalWaves, RESET)
            );
            messenger.tell(sender, String.join("\n", lines));
        });

        return true;
    }

    @Override
    public List<String> tab(ArenaMaster am, Player player, String... args) {
        // TODO: tab complete arena slugs?
        return Command.super.tab(am, player, args);
    }

}
