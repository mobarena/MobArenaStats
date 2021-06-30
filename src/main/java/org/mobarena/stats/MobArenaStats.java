package org.mobarena.stats;

import org.mobarena.stats.store.StatsStore;
import org.mobarena.stats.store.StatsStoreRegistry;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.logging.Logger;

public interface MobArenaStats {

    Logger getLogger();

    Executor getSyncExecutor();

    Executor getAsyncExecutor();

    StatsStore getStatsStore();

    StatsStoreRegistry getStatsStoreRegistry();

    File getDataFolder();

}
