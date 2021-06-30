package org.mobarena.stats.platform;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.concurrent.Executor;

public class AsyncBukkitExecutor implements Executor {

    private final Plugin plugin;
    private final BukkitScheduler scheduler;

    public AsyncBukkitExecutor(Plugin plugin, BukkitScheduler scheduler) {
        this.plugin = plugin;
        this.scheduler = scheduler;
    }

    @Override
    public void execute(Runnable command) {
        scheduler.runTaskAsynchronously(plugin, command);
    }

}
