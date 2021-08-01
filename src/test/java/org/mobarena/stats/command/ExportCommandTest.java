package org.mobarena.stats.command;

import com.garbagemule.MobArena.Messenger;
import com.garbagemule.MobArena.framework.ArenaMaster;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mobarena.stats.MobArenaStats;
import org.mobarena.stats.store.StatsStore;
import org.mobarena.stats.store.StatsStoreRegistry;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExportCommandTest {

    MobArenaStats plugin;
    ExportCommand subject;

    @BeforeEach
    void setup() {
        plugin = mock(MobArenaStats.class);
        subject = new ExportCommand(plugin);
    }

    @Test
    void failure() throws Exception {
        ArenaMaster am = mock(ArenaMaster.class);
        CommandSender sender = mock(CommandSender.class);
        String[] args = {};
        Messenger messenger = mock(Messenger.class);
        StatsStore source = mock(StatsStore.class);
        StatsStore target = mock(StatsStore.class);
        StatsStoreRegistry registry = mock(StatsStoreRegistry.class);
        when(am.getGlobalMessenger()).thenReturn(messenger);
        when(plugin.getStatsStore()).thenReturn(source);
        when(plugin.getStatsStoreRegistry()).thenReturn(registry);
        when(plugin.getAsyncExecutor()).thenReturn(Runnable::run);
        when(registry.create(ArgumentMatchers.any(ConfigurationSection.class))).thenReturn(target);
        doThrow(IOException.class).when(source).export(target);

        boolean result = subject.execute(am, sender, args);

        assertThat(result, equalTo(true));
        verify(messenger).tell(eq(sender), contains("Exporting stats"));
        verify(messenger).tell(eq(sender), contains("failed"));
    }

    @Test
    void success() throws Exception {
        ArenaMaster am = mock(ArenaMaster.class);
        CommandSender sender = mock(CommandSender.class);
        String[] args = {};
        Messenger messenger = mock(Messenger.class);
        StatsStore source = mock(StatsStore.class);
        StatsStore target = mock(StatsStore.class);
        StatsStoreRegistry registry = mock(StatsStoreRegistry.class);
        when(am.getGlobalMessenger()).thenReturn(messenger);
        when(plugin.getStatsStore()).thenReturn(source);
        when(plugin.getStatsStoreRegistry()).thenReturn(registry);
        when(plugin.getAsyncExecutor()).thenReturn(Runnable::run);
        when(registry.create(ArgumentMatchers.any(ConfigurationSection.class))).thenReturn(target);

        boolean result = subject.execute(am, sender, args);

        assertThat(result, equalTo(true));
        verify(messenger).tell(eq(sender), contains("Exporting stats"));
        verify(messenger).tell(eq(sender), contains("complete"));
    }

}
