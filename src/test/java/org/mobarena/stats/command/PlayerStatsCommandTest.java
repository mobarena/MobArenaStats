package org.mobarena.stats.command;

import com.garbagemule.MobArena.Messenger;
import com.garbagemule.MobArena.framework.ArenaMaster;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mobarena.stats.MobArenaStats;
import org.mobarena.stats.store.PlayerStats;
import org.mobarena.stats.store.StatsStore;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerStatsCommandTest {

    MobArenaStats plugin;
    PlayerStatsCommand subject;

    @BeforeEach
    void setup() {
        plugin = mock(MobArenaStats.class);
        subject = new PlayerStatsCommand(plugin);
    }

    @Test
    void noArgumentsReturnsFalseForConsole() {
        ArenaMaster am = mock(ArenaMaster.class);
        CommandSender sender = mock(CommandSender.class);
        String[] args = {};

        boolean result = subject.execute(am, sender, args);

        assertThat(result, equalTo(false));
    }

    @Test
    void noArgumentsUsesSenderNameForPlayers() {
        String name = "alice";
        ArenaMaster am = mock(ArenaMaster.class);
        CommandSender sender = mock(Player.class);
        String[] args = {};
        Messenger messenger = mock(Messenger.class);
        StatsStore store = mock(StatsStore.class);
        PlayerStats stats = new PlayerStats(1, 2, 3, 4);
        when(sender.getName()).thenReturn(name);
        when(am.getGlobalMessenger()).thenReturn(messenger);
        when(plugin.getStatsStore()).thenReturn(store);
        when(plugin.getAsyncExecutor()).thenReturn(Runnable::run);
        when(store.getPlayerStats(name)).thenReturn(stats);

        boolean result = subject.execute(am, sender, args);

        assertThat(result, equalTo(true));
        verify(messenger).tell(eq(sender), contains(name));
    }

    @Test
    void success() {
        String name = "garbagemule";
        ArenaMaster am = mock(ArenaMaster.class);
        CommandSender sender = mock(CommandSender.class);
        String[] args = {name};
        Messenger messenger = mock(Messenger.class);
        StatsStore store = mock(StatsStore.class);
        PlayerStats stats = new PlayerStats(1, 2, 3, 4);
        when(am.getGlobalMessenger()).thenReturn(messenger);
        when(plugin.getStatsStore()).thenReturn(store);
        when(plugin.getAsyncExecutor()).thenReturn(Runnable::run);
        when(store.getPlayerStats(name)).thenReturn(stats);

        boolean result = subject.execute(am, sender, args);

        assertThat(result, equalTo(true));
        verify(messenger).tell(eq(sender), contains(name));
    }

}
