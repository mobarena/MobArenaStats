package org.mobarena.stats.command;

import com.garbagemule.MobArena.Messenger;
import com.garbagemule.MobArena.framework.ArenaMaster;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mobarena.stats.MobArenaStats;
import org.mobarena.stats.store.StatsStore;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteSessionStatsCommandTest {

    MobArenaStats plugin;
    DeleteSessionStatsCommand subject;

    @BeforeEach
    void setup() {
        plugin = mock(MobArenaStats.class);
        subject = new DeleteSessionStatsCommand(plugin);
    }

    @Test
    void success() {
        String id = "cafebabe-ea75-dead-beef-deadcafebabe";
        ArenaMaster am = mock(ArenaMaster.class);
        CommandSender sender = mock(CommandSender.class);
        String[] args = {id};
        Messenger messenger = mock(Messenger.class);
        StatsStore store = mock(StatsStore.class);
        when(am.getGlobalMessenger()).thenReturn(messenger);
        when(plugin.getStatsStore()).thenReturn(store);
        when(plugin.getAsyncExecutor()).thenReturn(Runnable::run);

        boolean result = subject.execute(am, sender, args);

        assertThat(result, equalTo(true));
        verify(store).delete(UUID.fromString(id));
        verify(messenger).tell(eq(sender), contains("deleted"));
    }

}
