package org.mobarena.stats;

import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.commands.CommandHandler;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.mobarena.stats.command.ArenaStatsCommand;
import org.mobarena.stats.command.DeleteSessionStatsCommand;
import org.mobarena.stats.command.ExportCommand;
import org.mobarena.stats.command.GlobalStatsCommand;
import org.mobarena.stats.command.ImportCommand;
import org.mobarena.stats.command.PlayerStatsCommand;
import org.mobarena.stats.platform.AsyncBukkitExecutor;
import org.mobarena.stats.platform.SyncBukkitExecutor;
import org.mobarena.stats.session.SessionListener;
import org.mobarena.stats.session.SessionStore;
import org.mobarena.stats.store.StatsStore;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.mobarena.stats.store.StatsStoreRegistry;
import org.mobarena.stats.store.csv.CsvStatsStore;
import org.mobarena.stats.store.jdbc.JdbcStatsStore;
import org.mobarena.stats.store.mariadb.MariadbStatsStore;
import org.mobarena.stats.store.mysql.MysqlStatsStore;
import org.mobarena.stats.store.sqlite.SqliteStatsStore;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.logging.Level;

public class MobArenaStatsPlugin extends JavaPlugin implements MobArenaStats {

    // The sad state of affairs is that MobArena's command framework has no
    // support for registering commands by instance, but only by class, which
    // means that we can't properly inject dependencies and have to resort to
    // the Singleton Pattern.
    private static MobArenaStats instance;

    private StatsStoreRegistry statsStoreRegistry;

    private Executor syncExecutor;
    private Executor asyncExecutor;

    private SessionStore sessionStore;
    private StatsStore statsStore;
    private SessionListener sessionListener;

    @Override
    public void onLoad() {
        createStatsStoreRegistry();
        registerStatsStoreFactories();
    }

    private void createStatsStoreRegistry() {
        statsStoreRegistry = StatsStoreRegistry.create(this);
    }

    private void registerStatsStoreFactories() {
        statsStoreRegistry.register("csv", CsvStatsStore::create);
        statsStoreRegistry.register("jdbc", JdbcStatsStore::create);
        statsStoreRegistry.register("sqlite", SqliteStatsStore::create);
        statsStoreRegistry.register("mysql", MysqlStatsStore::create);
        statsStoreRegistry.register("mariadb", MariadbStatsStore::create);
    }

    @Override
    public void onEnable() {
        setup();
        reload();
    }

    private void setup() {
        try {
            instance = this;

            createDataFolder();
            createConfigFile();
            setupExecutors();
            setupCommands();
        } catch (Exception up) {
            // If setup fails, we can't recover, so throw up
            throw new RuntimeException(up);
        }
    }

    private void createDataFolder() {
        File dir = getDataFolder();
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                throw new IllegalStateException("Failed to create plugin data folder!");
            }
            getLogger().info("Data folder created.");
        }
    }

    private void createConfigFile() {
        File file = new File(getDataFolder(), "config.yml");
        if (!file.exists()) {
            saveResource("config.yml", false);
            getLogger().info("config.yml created.");
        }
    }

    private void setupExecutors() {
        BukkitScheduler scheduler = getServer().getScheduler();
        if (syncExecutor == null) {
            syncExecutor = new SyncBukkitExecutor(this, scheduler);
        }
        if (asyncExecutor == null) {
            asyncExecutor = new AsyncBukkitExecutor(this, scheduler);
        }
    }

    private void setupCommands() {
        PluginManager manager = getServer().getPluginManager();
        MobArena mobarena = (MobArena) manager.getPlugin("MobArena");

        PluginCommand command = mobarena.getCommand("ma");
        CommandHandler handler = (CommandHandler) command.getExecutor();

        // User commands
        handler.register(ArenaStatsCommand.class);
        handler.register(GlobalStatsCommand.class);
        handler.register(PlayerStatsCommand.class);

        // Admin commands
        handler.register(DeleteSessionStatsCommand.class);
        handler.register(ExportCommand.class);
        handler.register(ImportCommand.class);
    }

    private void reload() {
        try {
            createSessionStore();
            loadStatsStore();
            registerSessionListener();
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Load failure", e);
        }
    }

    private void createSessionStore() {
        if (sessionStore != null) {
            sessionStore.clear();
        }

        sessionStore = SessionStore.createNew();
        getLogger().info("Session store created.");
    }

    private void loadStatsStore() throws Exception {
        ConfigurationSection config = getConfig();
        ConfigurationSection section = config.getConfigurationSection("store");
        if (section == null) {
            throw new IllegalArgumentException("No store section in config-file.");
        }

        statsStore = statsStoreRegistry.create(section);
    }

    private void registerSessionListener() {
        if (sessionListener != null) {
            HandlerList.unregisterAll(sessionListener);
        }

        sessionListener = new SessionListener(sessionStore, statsStore, asyncExecutor, getLogger());
        getServer().getPluginManager().registerEvents(sessionListener, this);
        getLogger().info("Session listener registered.");
    }

    public StatsStoreRegistry getStatsStoreRegistry() {
        return statsStoreRegistry;
    }

    public Executor getSyncExecutor() {
        return syncExecutor;
    }

    public Executor getAsyncExecutor() {
        return asyncExecutor;
    }

    public StatsStore getStatsStore() {
        return statsStore;
    }

    public static MobArenaStats getInstance() {
        return instance;
    }

}
