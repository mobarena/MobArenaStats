package org.mobarena.stats.command;

import com.garbagemule.MobArena.Messenger;
import com.garbagemule.MobArena.commands.Command;
import com.garbagemule.MobArena.commands.CommandInfo;
import com.garbagemule.MobArena.framework.ArenaMaster;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mobarena.stats.MobArenaStats;
import org.mobarena.stats.store.StatsExport;
import org.mobarena.stats.store.StatsImport;
import org.mobarena.stats.store.StatsStore;
import org.mobarena.stats.store.StatsStoreRegistry;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@CommandInfo(
    name = "import-stats",
    pattern = "import-stats",
    usage = "/ma import-stats <filename>",
    desc = "import stats from an database export file into the current stats store",
    permission = "mobarenastats.command.import-stats"
)
public class ImportCommand implements Command {

    private final MobArenaStats plugin;

    public ImportCommand(MobArenaStats plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        if (args.length < 1) {
            return false;
        }

        Path data = plugin.getDataFolder().toPath();
        Path file = data.resolve(args[0]);
        if (!Files.exists(file)) {
            sender.sendMessage(String.format(
                "File %s%s%s not found.",
                ChatColor.YELLOW,
                args[0],
                ChatColor.RESET
            ));
            return false;
        }

        String filename = file.getFileName().toString();
        if (!filename.startsWith(StatsExport.FILENAME_PREFIX)) {
            sender.sendMessage(String.format(
                "Not a valid database export; filename must start with %s%s%s.",
                ChatColor.YELLOW,
                StatsExport.FILENAME_PREFIX,
                ChatColor.RESET
            ));
            return true;
        }
        if (!filename.endsWith(StatsExport.FILENAME_SUFFIX)) {
            sender.sendMessage(String.format(
                "Not a valid database export; filename must end with %s%s%s.",
                ChatColor.YELLOW,
                StatsExport.FILENAME_SUFFIX,
                ChatColor.RESET
            ));
            return true;
        }

        StatsStore store = plugin.getStatsStore();
        StatsStoreRegistry registry = plugin.getStatsStoreRegistry();

        Messenger messenger = am.getGlobalMessenger();
        messenger.tell(sender, String.format(
            "Importing stats from %s%s%s. This may take a while...",
            ChatColor.YELLOW,
            filename,
            ChatColor.RESET
        ));
        plugin.getAsyncExecutor().execute(() -> {
            try {
                StatsImport.run(registry, filename, store);

                messenger.tell(sender, String.format(
                    "Import from %s%s%s complete.",
                    ChatColor.YELLOW,
                    filename,
                    ChatColor.RESET
                ));
            } catch (Exception e) {
                messenger.tell(sender, String.format(
                    "Import %sfailed%s because:\n%s",
                    ChatColor.RED,
                    ChatColor.RESET,
                    e.getMessage()
                ));
            }
        });

        return true;
    }

    @Override
    public List<String> tab(ArenaMaster am, Player player, String... args) {
        if (args.length > 1) {
            return Collections.emptyList();
        }

        String[] files = plugin.getDataFolder().list();
        if (files == null || files.length == 0) {
            return Collections.emptyList();
        }

        String prefix = (args.length == 1) ? args[0] : "";

        return Arrays.stream(files)
            .filter(filename -> filename.startsWith(prefix))
            .filter(filename -> filename.startsWith(StatsExport.FILENAME_PREFIX))
            .filter(filename -> filename.endsWith(StatsExport.FILENAME_SUFFIX))
            .collect(Collectors.toList());
    }

}
