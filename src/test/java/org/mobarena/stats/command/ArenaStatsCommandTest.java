package org.mobarena.stats.command;

import com.garbagemule.MobArena.Messenger;
import com.garbagemule.MobArena.framework.ArenaMaster;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mobarena.stats.MobArenaStats;
import org.mobarena.stats.store.ArenaStats;
import org.mobarena.stats.store.StatsStore;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArenaStatsCommandTest {

    MobArenaStats plugin;
    ArenaStatsCommand subject;

    @BeforeEach
    void setup() {
        plugin = mock(MobArenaStats.class);
        subject = new ArenaStatsCommand(plugin);
    }

    @Test
    void noArgumentsReturnsFalse() {
        ArenaMaster am = mock(ArenaMaster.class);
        CommandSender sender = mock(CommandSender.class);
        String[] args = {};

        boolean result = subject.execute(am, sender, args);

        assertThat(result, equalTo(false));
    }

    @Test
    void success() {
        String slug = "castle";
        ArenaMaster am = mock(ArenaMaster.class);
        CommandSender sender = mock(CommandSender.class);
        String[] args = {slug};
        Messenger messenger = mock(Messenger.class);
        StatsStore store = mock(StatsStore.class);
        ArenaStats stats = new ArenaStats(1, 2, 3, 4, 5, 6, 7);
        when(am.getGlobalMessenger()).thenReturn(messenger);
        when(plugin.getStatsStore()).thenReturn(store);
        when(plugin.getAsyncExecutor()).thenReturn(Runnable::run);
        when(store.getArenaStats(slug)).thenReturn(stats);

        boolean result = subject.execute(am, sender, args);

        assertThat(result, equalTo(true));
        verify(messenger).tell(eq(sender), contains(slug));
    }

}
