package org.mobarena.stats.store;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class StatsImport {

    public static void run(
        StatsStoreRegistry registry,
        String filename,
        StatsStore store
    ) throws Exception {
        ConfigurationSection config = new YamlConfiguration();
        config.set("type", "sqlite");
        config.set("filename", filename);
        StatsStore source = registry.create(config);

        source.export(store);
    }

}
