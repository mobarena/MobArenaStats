package org.mobarena.stats.platform;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.concurrent.Executor;

public class SyncBukkitExecutor implements Executor {

    private final Plugin plugin;
    private final BukkitScheduler scheduler;

    public SyncBukkitExecutor(Plugin plugin, BukkitScheduler scheduler) {
        this.plugin = plugin;
        this.scheduler = scheduler;
    }

    @Override
    public void execute(Runnable command) {
        scheduler.runTask(plugin, command);
    }

}
