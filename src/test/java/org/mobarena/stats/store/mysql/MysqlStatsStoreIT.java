package org.mobarena.stats.store.mysql;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.mobarena.stats.MobArenaStats;
import org.mobarena.stats.store.StatsStore;
import org.mobarena.stats.store.StatsStoreIT;
import org.mobarena.stats.store.jdbc.JdbcStatsStore;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.logging.Logger;

import static org.mockito.Mockito.*;

@SuppressWarnings("rawtypes")
@Testcontainers
public class MysqlStatsStoreIT extends StatsStoreIT {

    @Container
    static final MySQLContainer mysql = new MySQLContainer("mysql:5.7");

    static StatsStore subject;

    @BeforeAll
    static void setup() throws Exception {
        // Set up fake configuration
        ConfigurationSection config = new MemoryConfiguration();
        config.set("type", "mysql");
        config.set("url", mysql.getJdbcUrl());
        config.set("username", mysql.getUsername());
        config.set("password", mysql.getPassword());

        // Set up fake plugin
        Logger log = mock(Logger.class);
        MobArenaStats plugin = mock(MobArenaStats.class);
        when(plugin.getLogger()).thenReturn(log);

        // Create a real store test subject
        subject = JdbcStatsStore.create(config, plugin);
    }

    @Override
    public StatsStore getStore() {
        return subject;
    }

}
