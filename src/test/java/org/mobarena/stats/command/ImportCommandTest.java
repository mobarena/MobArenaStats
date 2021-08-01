package org.mobarena.stats.command;

import com.garbagemule.MobArena.Messenger;
import com.garbagemule.MobArena.framework.ArenaMaster;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mobarena.stats.MobArenaStats;
import org.mobarena.stats.store.StatsStore;
import org.mobarena.stats.store.StatsStoreRegistry;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImportCommandTest {

    @TempDir
    File data;

    MobArenaStats plugin;
    ImportCommand subject;

    @BeforeEach
    void setup() {
        plugin = mock(MobArenaStats.class);
        subject = new ImportCommand(plugin);
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
    void nonExistentSourceFileNoImport() {
        String filename = "stats.export-321.db";
        ArenaMaster am = mock(ArenaMaster.class);
        CommandSender sender = mock(CommandSender.class);
        String[] args = {filename};
        when(plugin.getDataFolder()).thenReturn(data);

        boolean result = subject.execute(am, sender, args);

        assertThat(result, equalTo(false));
        verify(sender).sendMessage(contains("not found"));
    }

    @Test
    void wrongFilenamePrefixNoImport() throws Exception {
        String filename = "stats.db";
        Files.createFile(data.toPath().resolve(filename));
        ArenaMaster am = mock(ArenaMaster.class);
        CommandSender sender = mock(CommandSender.class);
        String[] args = {filename};
        when(plugin.getDataFolder()).thenReturn(data);

        boolean result = subject.execute(am, sender, args);

        assertThat(result, equalTo(true));
        verify(sender).sendMessage(contains("filename must start with"));
    }

    @Test
    void wrongFilenameSuffixNoImport() throws Exception {
        String filename = "stats.export-123.sql";
        Files.createFile(data.toPath().resolve(filename));
        ArenaMaster am = mock(ArenaMaster.class);
        CommandSender sender = mock(CommandSender.class);
        String[] args = {filename};
        when(plugin.getDataFolder()).thenReturn(data);

        boolean result = subject.execute(am, sender, args);

        assertThat(result, equalTo(true));
        verify(sender).sendMessage(contains("filename must end with"));
    }

    @Test
    void failure() throws Exception {
        String filename = "stats.export-123.db";
        Files.createFile(data.toPath().resolve(filename));
        ArenaMaster am = mock(ArenaMaster.class);
        CommandSender sender = mock(CommandSender.class);
        String[] args = {filename};
        Messenger messenger = mock(Messenger.class);
        StatsStore source = mock(StatsStore.class);
        StatsStore target = mock(StatsStore.class);
        StatsStoreRegistry registry = mock(StatsStoreRegistry.class);
        when(am.getGlobalMessenger()).thenReturn(messenger);
        when(plugin.getDataFolder()).thenReturn(data);
        when(plugin.getStatsStore()).thenReturn(target);
        when(plugin.getStatsStoreRegistry()).thenReturn(registry);
        when(plugin.getAsyncExecutor()).thenReturn(Runnable::run);
        when(registry.create(ArgumentMatchers.any(ConfigurationSection.class))).thenReturn(source);
        doThrow(IOException.class).when(source).export(target);

        boolean result = subject.execute(am, sender, args);

        assertThat(result, equalTo(true));
        verify(messenger).tell(eq(sender), contains("Importing stats"));
        verify(messenger).tell(eq(sender), contains("failed"));
    }

    @Test
    void success() throws Exception {
        String filename = "stats.export-123.db";
        Files.createFile(data.toPath().resolve(filename));
        ArenaMaster am = mock(ArenaMaster.class);
        CommandSender sender = mock(CommandSender.class);
        String[] args = {filename};
        Messenger messenger = mock(Messenger.class);
        StatsStore source = mock(StatsStore.class);
        StatsStore target = mock(StatsStore.class);
        StatsStoreRegistry registry = mock(StatsStoreRegistry.class);
        when(am.getGlobalMessenger()).thenReturn(messenger);
        when(plugin.getDataFolder()).thenReturn(data);
        when(plugin.getStatsStore()).thenReturn(target);
        when(plugin.getStatsStoreRegistry()).thenReturn(registry);
        when(plugin.getAsyncExecutor()).thenReturn(Runnable::run);
        when(registry.create(ArgumentMatchers.any(ConfigurationSection.class))).thenReturn(source);

        boolean result = subject.execute(am, sender, args);

        assertThat(result, equalTo(true));
        verify(messenger).tell(eq(sender), contains("Importing stats"));
        verify(source).export(target);
        verify(messenger).tell(eq(sender), contains("complete"));
    }

}
