package org.mobarena.stats.store;

import org.bukkit.configuration.ConfigurationSection;
import org.mobarena.stats.MobArenaStatsPlugin;

import java.util.HashMap;
import java.util.Map;

public class StatsStoreRegistry {

    private final Map<String, StatsStoreFactory> typeToFactory;
    private final MobArenaStatsPlugin plugin;

    StatsStoreRegistry(MobArenaStatsPlugin plugin) {
        this.typeToFactory = new HashMap<>();
        this.plugin = plugin;
    }

    public void register(String type, StatsStoreFactory factory) {
        typeToFactory.put(type.toLowerCase(), factory);
    }

    public StatsStore create(ConfigurationSection config) throws Exception {
        String type = config.getString("type");
        if (type == null || type.isEmpty()) {
            throw new IllegalArgumentException("Missing 'type' in store configuration");
        }

        StatsStoreFactory factory = typeToFactory.get(type.toLowerCase());
        if (factory == null) {
            throw new IllegalArgumentException("Unknown store type: " + type);
        }

        return factory.create(config, plugin);
    }

    public static StatsStoreRegistry create(MobArenaStatsPlugin plugin) {
        return new StatsStoreRegistry(plugin);
    }

}
