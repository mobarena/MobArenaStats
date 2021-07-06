package org.mobarena.stats.command;

import com.garbagemule.MobArena.Messenger;
import com.garbagemule.MobArena.commands.Command;
import com.garbagemule.MobArena.commands.CommandInfo;
import com.garbagemule.MobArena.framework.ArenaMaster;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mobarena.stats.MobArenaStats;
import org.mobarena.stats.store.StatsStore;

import java.util.List;
import java.util.UUID;

@CommandInfo(
    name = "delete-session-stats",
    pattern = "delete-session-stats",
    usage = "/ma delete-session-stats <session-id>",
    desc = "delete all stats collected for the given session",
    permission = "mobarenastats.command.delete-session-stats"
)
public class DeleteSessionStatsCommand implements Command {

    private final MobArenaStats plugin;

    public DeleteSessionStatsCommand(MobArenaStats plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        // TODO: check args, handle non-UUID input error
        UUID sessionId = UUID.fromString(args[0]);

        StatsStore store = plugin.getStatsStore();
        Messenger messenger = am.getGlobalMessenger();

        plugin.getAsyncExecutor().execute(() -> {
            store.delete(sessionId);
            messenger.tell(sender, String.format(
                "Stats for session %s%s%s deleted.",
                ChatColor.YELLOW,
                sessionId,
                ChatColor.RESET
            ));
        });

        return true;
    }

    @Override
    public List<String> tab(ArenaMaster am, Player player, String... args) {
        // TODO: tab complete session IDs?
        return Command.super.tab(am, player, args);
    }

}
