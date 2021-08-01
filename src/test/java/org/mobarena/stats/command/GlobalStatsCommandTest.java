package org.mobarena.stats.command;

import com.garbagemule.MobArena.Messenger;
import com.garbagemule.MobArena.framework.ArenaMaster;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mobarena.stats.MobArenaStats;
import org.mobarena.stats.store.GlobalStats;
import org.mobarena.stats.store.StatsStore;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalStatsCommandTest {

    MobArenaStats plugin;
    GlobalStatsCommand subject;

    @BeforeEach
    void setup() {
        plugin = mock(MobArenaStats.class);
        subject = new GlobalStatsCommand(plugin);
    }

    @Test
    void success() {
        ArenaMaster am = mock(ArenaMaster.class);
        CommandSender sender = mock(CommandSender.class);
        String[] args = {};
        Messenger messenger = mock(Messenger.class);
        StatsStore store = mock(StatsStore.class);
        GlobalStats stats = new GlobalStats(1, 2, 3, 4);
        when(am.getGlobalMessenger()).thenReturn(messenger);
        when(plugin.getStatsStore()).thenReturn(store);
        when(plugin.getAsyncExecutor()).thenReturn(Runnable::run);
        when(store.getGlobalStats()).thenReturn(stats);

        boolean result = subject.execute(am, sender, args);

        assertThat(result, equalTo(true));
        verify(messenger).tell(eq(sender), contains("Global stats"));
    }

}
