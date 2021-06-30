package org.mobarena.stats.store;

import org.bukkit.configuration.ConfigurationSection;
import org.mobarena.stats.MobArenaStats;

@FunctionalInterface
public interface StatsStoreFactory {

    /**
     * Create a new stats store from the given configuration.
     *
     * @param config a configuration to set up the stats store
     * @param plugin a plugin instance for dependencies
     * @return a new stats store instance
     * @throws Exception if store creation fails
     */
    StatsStore create(
        ConfigurationSection config,
        MobArenaStats plugin
    ) throws Exception;

}
