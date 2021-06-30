package org.mobarena.stats.command;

import com.garbagemule.MobArena.commands.Command;
import com.garbagemule.MobArena.commands.CommandInfo;
import com.garbagemule.MobArena.framework.ArenaMaster;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mobarena.stats.MobArenaStats;
import org.mobarena.stats.MobArenaStatsPlugin;
import org.mobarena.stats.store.PlayerStats;
import org.mobarena.stats.store.StatsStore;

import java.util.List;

@CommandInfo(
    name = "player-stats",
    pattern = "player-stats",
    usage = "/ma player-stats <player>",
    desc = "show overall stats for the given player",
    permission = "mobarenastats.command.player-stats"
)
public class PlayerStatsCommand implements Command {

    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        // :(
        MobArenaStats plugin = MobArenaStatsPlugin.getInstance();

        String name;
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                return false;
            }
            name = sender.getName();
        } else {
            name = args[0];
        }

        plugin.getAsyncExecutor().execute(() -> {
            StatsStore store = plugin.getStatsStore();
            PlayerStats stats = store.getPlayerStats(name);
            sender.sendMessage("Stats for player " + name + ":");
            sender.sendMessage("- Total sessions: " + stats.totalSessions);
            sender.sendMessage("- Total duration: " + stats.totalSeconds + " secs");
            sender.sendMessage("- Total kills: " + stats.totalKills);
            sender.sendMessage("- Total waves: " + stats.totalWaves);
        });

        return true;
    }

    @Override
    public List<String> tab(ArenaMaster am, Player player, String... args) {
        // TODO: tab complete player names?
        return Command.super.tab(am, player, args);
    }

}
