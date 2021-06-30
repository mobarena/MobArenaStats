package org.mobarena.stats.store;

import org.bukkit.configuration.ConfigurationSection;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class StatsExportTest {

    @Test
    void exportsToTargetStore() throws Exception {
        StatsStore store = mock(StatsStore.class);
        StatsStore target = mock(StatsStore.class);
        StatsStoreRegistry registry = mock(StatsStoreRegistry.class);
        when(registry.create(any(ConfigurationSection.class))).thenReturn(target);

        StatsExport.run(store, registry);

        verify(store).export(target);
    }

}
