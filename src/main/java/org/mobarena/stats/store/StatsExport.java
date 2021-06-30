package org.mobarena.stats.store;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class StatsExport {

    public static final String FILENAME_PREFIX = "stats.export-";
    public static final String FILENAME_SUFFIX = ".db";

    public static String run(
        StatsStore store,
        StatsStoreRegistry registry
    ) throws Exception {
        String filename = FILENAME_PREFIX + System.currentTimeMillis() + FILENAME_SUFFIX;

        ConfigurationSection config = new YamlConfiguration();
        config.set("type", "sqlite");
        config.set("filename", filename);
        StatsStore output = registry.create(config);

        store.export(output);

        return filename;
    }

}
