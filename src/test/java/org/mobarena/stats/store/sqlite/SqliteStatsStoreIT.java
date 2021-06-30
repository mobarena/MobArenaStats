package org.mobarena.stats.store.sqlite;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.io.TempDir;
import org.mobarena.stats.MobArenaStats;
import org.mobarena.stats.store.StatsStore;
import org.mobarena.stats.store.StatsStoreIT;

import java.io.File;
import java.util.logging.Logger;

import static org.mockito.Mockito.*;

public class SqliteStatsStoreIT extends StatsStoreIT {

    @TempDir
    static File data;

    static StatsStore subject;

    @BeforeAll
    static void setup() throws Exception {
        // Set up fake configuration
        ConfigurationSection config = new MemoryConfiguration();
        config.set("type", "sqlite");

        // Set up fake plugin
        Logger log = mock(Logger.class);
        MobArenaStats plugin = mock(MobArenaStats.class);
        when(plugin.getLogger()).thenReturn(log);
        when(plugin.getDataFolder()).thenReturn(data);

        // Create a real store test subject
        subject = SqliteStatsStore.create(config, plugin);
    }

    @Override
    public StatsStore getStore() {
        return subject;
    }

}
