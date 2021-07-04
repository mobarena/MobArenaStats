package org.mobarena.stats.metrics;

import org.bstats.charts.SimplePie;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.mobarena.stats.MobArenaStatsPlugin;
import org.mobarena.stats.store.StatsStore;

public class StoreTypeChart extends SimplePie {

    public StoreTypeChart(MobArenaStatsPlugin plugin) {
        super("store_type", () -> {
            StatsStore store = plugin.getStatsStore();
            if (store == null) {
                return null;
            }

            // If the store isn't null, it means whatever type is set in
            // the config-file is valid and has already been parsed, so
            // we can just extract and submit it.
            Configuration config = plugin.getConfig();
            ConfigurationSection section = config.getConfigurationSection("store");
            return section.getString("type");
        });
    }

}
