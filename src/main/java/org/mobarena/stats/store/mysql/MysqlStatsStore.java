package org.mobarena.stats.store.mysql;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.mobarena.stats.MobArenaStats;
import org.mobarena.stats.store.jdbc.JdbcStatsStore;

public class MysqlStatsStore {

    public static JdbcStatsStore create(
        ConfigurationSection config,
        MobArenaStats plugin
    ) throws Exception {
        ConfigurationSection copy = new MemoryConfiguration();
        for (String key : config.getKeys(false)) {
            copy.set(key, config.get(key));
        }

        String url = getUrl(copy);
        copy.set("type", "mysql");
        copy.set("url", url);

        return JdbcStatsStore.create(copy, plugin);
    }

    static String getUrl(ConfigurationSection config) {
        String host = config.getString("host", "localhost");
        int port = config.getInt("port", 3306);
        String database = config.getString("database", "mobarena_stats");
        boolean ssl = config.getBoolean("ssl", false);

        String params = "useSSL=" + ssl;

        return "jdbc:mysql://" + host + ":" + port + "/" + database + "?" + params;
    }

}
