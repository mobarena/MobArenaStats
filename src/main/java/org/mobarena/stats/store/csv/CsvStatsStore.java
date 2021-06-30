package org.mobarena.stats.store.csv;

import org.bukkit.configuration.ConfigurationSection;
import org.mobarena.stats.MobArenaStats;
import org.mobarena.stats.session.PlayerConclusion;
import org.mobarena.stats.session.PlayerSessionStats;
import org.mobarena.stats.session.Session;
import org.mobarena.stats.session.SessionConclusion;
import org.mobarena.stats.session.SessionStats;
import org.mobarena.stats.store.ArenaStats;
import org.mobarena.stats.store.GlobalStats;
import org.mobarena.stats.store.PlayerStats;
import org.mobarena.stats.store.StatsStore;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.logging.Logger;

public class CsvStatsStore implements StatsStore {

    private static final String[] SESSION_FIELDS = {
        "session_id",
        "arena_slug",
        "start_time",
        "end_time",
        "last_wave",
        "conclusion"
    };

    private static final String[] PLAYER_SESSION_FIELDS = {
        "session_id",
        "player_id",
        "player_name",
        "class",
        "join_time",
        "ready_time",
        "leave_time",
        "death_time",
        "kills",
        "dmg_done",
        "dmg_taken",
        "swings",
        "hits",
        "last_wave",
        "conclusion"
    };

    private final File folder;
    private final File sessionsFile;
    private final File playersFile;
    private final String separator;
    private final DateTimeFormatter formatter;
    private final Logger log;

    private CsvStatsStore(
        File folder,
        String separator,
        Logger log
    ) {
        this.folder = folder;
        this.sessionsFile = new File(folder, "sessions.csv");
        this.playersFile = new File(folder, "players.csv");
        this.separator = separator;
        this.formatter = DateTimeFormatter.ISO_INSTANT;
        this.log = log;
    }

    @Override
    public void save(Session session) throws IOException {
        try {
            createDataFolder();
            saveArenaSession(session);
            savePlayerSessions(session);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public void delete(UUID sessionId) {
        throw new UnsupportedOperationException("Session deletion is not supported by the CSV data store");
    }

    private void createDataFolder() {
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                throw new IllegalStateException("Failed to create stats data folder");
            }
        }
    }

    private void saveArenaSession(Session session) throws Exception {
        boolean writeHeader = !sessionsFile.exists();

        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(sessionsFile, true)))) {
            if (writeHeader) {
                String line = String.join(separator, SESSION_FIELDS);
                writer.println(line);
            }

            SessionStats stats = session.getSessionStats();
            String line = String.join(
                separator,
                stats.sessionId.toString(),
                stats.arenaSlug,
                formatter.format(stats.startTime),
                formatter.format(stats.endTime),
                String.valueOf(stats.lastWave),
                String.valueOf(stats.conclusion)
            );
            writer.println(line);
            log.info("Session stats written to disk (" + stats.sessionId + ").");
        }
    }

    private void savePlayerSessions(Session session) throws Exception {
        boolean writeHeader = !playersFile.exists();

        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(playersFile, true)))) {
            if (writeHeader) {
                String line = String.join(separator, PLAYER_SESSION_FIELDS);
                writer.println(line);
            }

            for (PlayerSessionStats stats : session.getPlayerStats()) {
                String line = String.join(
                    separator,
                    stats.sessionId.toString(),
                    stats.playerId.toString(),
                    stats.playerName,
                    stats.className,
                    formatter.format(stats.joinTime),
                    formatter.format(stats.readyTime),
                    stats.leaveTime != null ? formatter.format(stats.leaveTime) : "",
                    stats.deathTime != null ? formatter.format(stats.deathTime) : "",
                    String.valueOf(stats.kills),
                    String.valueOf(stats.dmgDone),
                    String.valueOf(stats.dmgTaken),
                    String.valueOf(stats.swings),
                    String.valueOf(stats.hits),
                    String.valueOf(stats.lastWave),
                    String.valueOf(stats.conclusion)
                );
                writer.println(line);
            }
            log.info("Player stats written to disk (" + session.getSessionStats().sessionId + ").");
        }
    }

    @Override
    public GlobalStats getGlobalStats() {
        throw new UnsupportedOperationException("Queries are not supported by the CSV data store");
    }

    @Override
    public ArenaStats getArenaStats(String slug) {
        throw new UnsupportedOperationException("Queries are not supported by the CSV data store");
    }

    @Override
    public PlayerStats getPlayerStats(String name) {
        throw new UnsupportedOperationException("Queries are not supported by the CSV data store");
    }

    @Override
    public void export(StatsStore target) throws IOException {
        List<String> sessionLines = Files.readAllLines(sessionsFile.toPath());
        List<String> playerLines = Files.readAllLines(playersFile.toPath());

        for (int i = 1; i < sessionLines.size(); i++) {
            String sessionLine = sessionLines.get(i);
            String[] sessionParts = sessionLine.split(separator);

            UUID sessionId = UUID.fromString(sessionParts[0]);
            String arenaSlug = sessionParts[1];
            Session session = new Session(sessionId, arenaSlug);
            {
                SessionStats stats = session.getSessionStats();
                stats.startTime = Instant.parse(sessionParts[2]);
                stats.endTime = Instant.parse(sessionParts[3]);
                stats.lastWave = Integer.parseInt(sessionParts[4]);
                stats.conclusion = SessionConclusion.valueOf(sessionParts[5]);
            }

            String prefix = sessionId + separator;
            for (int j = 1; j < playerLines.size(); j++) {
                String playerLine = playerLines.get(j);
                if (!playerLine.startsWith(prefix)) {
                    continue;
                }

                String[] playerParts = playerLine.split(separator);
                {
                    UUID playerId = UUID.fromString(playerParts[1]);
                    String playerName = playerParts[2];

                    PlayerSessionStats stats = new PlayerSessionStats(sessionId, playerId, playerName);
                    stats.className = playerParts[3];
                    stats.joinTime = safe(playerParts[4], Instant::parse);
                    stats.readyTime = safe(playerParts[5], Instant::parse);
                    stats.leaveTime = safe(playerParts[6], Instant::parse);
                    stats.deathTime = safe(playerParts[7], Instant::parse);
                    stats.kills = Integer.parseInt(playerParts[8]);
                    stats.dmgDone = Integer.parseInt(playerParts[9]);
                    stats.dmgTaken = Integer.parseInt(playerParts[10]);
                    stats.swings = Integer.parseInt(playerParts[11]);
                    stats.hits = Integer.parseInt(playerParts[12]);
                    stats.lastWave = Integer.parseInt(playerParts[13]);
                    stats.conclusion = safe(playerParts[14], PlayerConclusion::valueOf, PlayerConclusion.DEFEAT);

                    session.setPlayerStats(playerId, stats);
                }
            }

            target.save(session);
        }
    }

    private static <T, R> R safe(T value, Function<T, R> parser) {
        return safe(value, parser, null);
    }

    private static <T, R> R safe(T value, Function<T, R> parser, R def) {
        try {
            return parser.apply(value);
        } catch (Exception e) {
            return def;
        }
    }

    public static CsvStatsStore create(
        ConfigurationSection config,
        MobArenaStats plugin
    ) {
        String folder = config.getString("folder", "data");
        String separator = config.getString("separator", ";");

        File root = new File(plugin.getDataFolder(), folder);
        Logger log = plugin.getLogger();

        return new CsvStatsStore(root, separator, log);
    }

}
