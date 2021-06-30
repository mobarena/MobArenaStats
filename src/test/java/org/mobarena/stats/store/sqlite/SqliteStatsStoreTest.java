package org.mobarena.stats.store.sqlite;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;
import org.mobarena.stats.store.sqlite.SqliteStatsStore;

import java.io.File;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

class SqliteStatsStoreTest {

    @Test
    void getUrlDefaultValues() {
        ConfigurationSection config = new YamlConfiguration();
        File data = new File("data");

        String result = SqliteStatsStore.getUrl(config, data);

        String expected = "jdbc:sqlite:" + data.getPath() + "/stats.db";
        assertThat(result, equalTo(expected));
    }

    @Test
    void getUrlConstructsJdbcUrl() {
        ConfigurationSection config = new YamlConfiguration();
        config.set("filename", "HECK-YES.db");
        File data = new File("data");

        String result = SqliteStatsStore.getUrl(config, data);

        String expected = "jdbc:sqlite:" + data.getPath() + "/HECK-YES.db";
        assertThat(result, equalTo(expected));
    }

}
