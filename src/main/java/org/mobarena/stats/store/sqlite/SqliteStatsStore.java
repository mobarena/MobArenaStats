package org.mobarena.stats.store.sqlite;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.mobarena.stats.MobArenaStats;
import org.mobarena.stats.store.jdbc.JdbcStatsStore;

import java.io.File;

public class SqliteStatsStore {

    public static JdbcStatsStore create(
        ConfigurationSection config,
        MobArenaStats plugin
    ) throws Exception {
        ConfigurationSection copy = new MemoryConfiguration();
        for (String key : config.getKeys(false)) {
            copy.set(key, config.get(key));
        }

        File data = plugin.getDataFolder();
        String url = getUrl(copy, data);
        copy.set("type", "sqlite");
        copy.set("url", url);
        copy.addDefault("username", "sa");
        copy.addDefault("password", "");

        return JdbcStatsStore.create(copy, plugin);
    }

    static String getUrl(ConfigurationSection config, File data) {
        String folder = data.getPath();
        String filename = config.getString("filename", "stats.db");

        return "jdbc:sqlite:" + folder + "/" + filename;
    }

}
