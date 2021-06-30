package org.mobarena.stats.command;

import com.garbagemule.MobArena.Messenger;
import com.garbagemule.MobArena.commands.Command;
import com.garbagemule.MobArena.commands.CommandInfo;
import com.garbagemule.MobArena.framework.ArenaMaster;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.mobarena.stats.MobArenaStats;
import org.mobarena.stats.MobArenaStatsPlugin;
import org.mobarena.stats.store.StatsExport;
import org.mobarena.stats.store.StatsStore;
import org.mobarena.stats.store.StatsStoreRegistry;

@CommandInfo(
    name = "export-stats",
    pattern = "export-stats",
    usage = "/ma export-stats",
    desc = "export the current stats store to a file in the given format",
    permission = "mobarenastats.command.export-stats"
)
public class ExportCommand implements Command {

    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        // :(
        MobArenaStats plugin = MobArenaStatsPlugin.getInstance();

        StatsStore store = plugin.getStatsStore();
        StatsStoreRegistry registry = plugin.getStatsStoreRegistry();

        Messenger messenger = am.getGlobalMessenger();
        messenger.tell(sender, "Exporting stats. This may take a while...");
        plugin.getAsyncExecutor().execute(() -> {
            try {
                String filename = StatsExport.run(store, registry);

                messenger.tell(sender, String.format(
                    "Export to %s%s%s complete.",
                    ChatColor.YELLOW,
                    filename,
                    ChatColor.RESET
                ));
            } catch (Exception e) {
                messenger.tell(sender, String.format(
                    "Export %sfailed%s because:\n%s",
                    ChatColor.RED,
                    ChatColor.RESET,
                    e.getMessage()
                ));
            }
        });

        return true;
    }

}
