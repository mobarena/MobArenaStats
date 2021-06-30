package org.mobarena.stats.store;

import org.bukkit.configuration.ConfigurationSection;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class StatsImportTest {

    @Test
    void exportsFromSourceStore() throws Exception {
        String filename = "stats.export-1234.db";
        StatsStore store = mock(StatsStore.class);
        StatsStore source = mock(StatsStore.class);
        StatsStoreRegistry registry = mock(StatsStoreRegistry.class);
        when(registry.create(any(ConfigurationSection.class))).thenReturn(source);

        StatsImport.run(registry, filename, store);

        verify(source).export(store);
    }

}
