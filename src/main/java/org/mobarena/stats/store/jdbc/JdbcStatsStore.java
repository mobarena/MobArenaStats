package org.mobarena.stats.store.jdbc;

import org.bukkit.configuration.ConfigurationSection;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.PreparedBatch;
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
import org.mobarena.stats.util.ResourceLoader;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.logging.Logger;

import static org.mobarena.stats.store.jdbc.Statement.DELETE_SESSION_DATA;
import static org.mobarena.stats.store.jdbc.Statement.FIND_ARENA_STATS;
import static org.mobarena.stats.store.jdbc.Statement.FIND_GLOBAL_STATS;
import static org.mobarena.stats.store.jdbc.Statement.FIND_PLAYER_SESSIONS_BY_ID;
import static org.mobarena.stats.store.jdbc.Statement.FIND_PLAYER_STATS;
import static org.mobarena.stats.store.jdbc.Statement.FIND_SESSIONS;
import static org.mobarena.stats.store.jdbc.Statement.INSERT_PLAYER_DATA;
import static org.mobarena.stats.store.jdbc.Statement.INSERT_SESSION_DATA;

public class JdbcStatsStore implements StatsStore {

    private final Jdbi jdbi;
    private final Statements statements;

    private JdbcStatsStore(Jdbi jdbi, Statements statements) {
        this.jdbi = jdbi;
        this.statements = statements;
    }

    @Override
    public synchronized void save(Session session) {
        jdbi.useTransaction(handle -> {
            // First the session data
            handle.createUpdate(statements.get(INSERT_SESSION_DATA))
                .bind("session_id", session.getSessionId().toString())
                .bind("arena_slug", session.getArenaSlug())
                .bind("start_time", session.getSessionStats().startTime)
                .bind("end_time", session.getSessionStats().endTime)
                .bind("last_wave", session.getSessionStats().lastWave)
                .bind("conclusion", session.getSessionStats().conclusion)
                .execute();

            // Then all of the player data
            PreparedBatch batch = handle.prepareBatch(statements.get(INSERT_PLAYER_DATA));
            for (PlayerSessionStats player : session.getPlayerStats()) {
                batch.bind("session_id", session.getSessionId().toString());
                batch.bind("player_id", player.playerId.toString());
                batch.bind("player_name", player.playerName);
                batch.bind("class", player.className);
                batch.bind("join_time", player.joinTime);
                batch.bind("ready_time", player.readyTime);
                batch.bind("leave_time", player.leaveTime);
                batch.bind("death_time", player.deathTime);
                batch.bind("kills", player.kills);
                batch.bind("dmg_done", player.dmgDone);
                batch.bind("dmg_taken", player.dmgTaken);
                batch.bind("swings", player.swings);
                batch.bind("hits", player.hits);
                batch.bind("last_wave", player.lastWave);
                batch.bind("conclusion", player.conclusion);
                batch.add();
            }
            batch.execute();
        });
    }

    @Override
    public void delete(UUID sessionId) {
        jdbi.useTransaction(handle -> handle
            .createUpdate(statements.get(DELETE_SESSION_DATA))
            .bind("session_id", sessionId.toString())
            .execute()
        );
    }

    @Override
    public GlobalStats getGlobalStats() {
        return jdbi.withHandle(handle -> handle
            .createQuery(statements.get(FIND_GLOBAL_STATS))
                .map((rs, ctx) -> new GlobalStats(
                    rs.getInt("total_sessions"),
                    rs.getLong("total_seconds"),
                    rs.getLong("total_kills"),
                    rs.getLong("total_waves")
                ))
                .first()
        );
    }

    @Override
    public ArenaStats getArenaStats(String slug) {
        return jdbi.withHandle(handle -> handle
            .createQuery(statements.get(FIND_ARENA_STATS))
            .bind("arena_slug", slug)
            .map((rs, ctx) -> new ArenaStats(
                rs.getInt("highest_wave"),
                rs.getInt("highest_seconds"),
                rs.getInt("highest_kills"),
                rs.getInt("total_sessions"),
                rs.getLong("total_seconds"),
                rs.getLong("total_kills"),
                rs.getLong("total_waves")
            ))
            .first()
        );
    }

    @Override
    public PlayerStats getPlayerStats(String name) {
        return jdbi.withHandle(handle -> handle
            .createQuery(statements.get(FIND_PLAYER_STATS))
            .bind("player_name", name)
            .map((rs, ctx) -> new PlayerStats(
                rs.getInt("total_sessions"),
                rs.getLong("total_seconds"),
                rs.getLong("total_kills"),
                rs.getLong("total_waves")
            ))
            .first()
        );
    }

    @Override
    public synchronized void export(StatsStore target) throws IOException {
        jdbi.useHandle(handle -> {
            int limit = 100;
            int offset = 0;

            while (true) {
                List<Session> sessions = handle.createQuery(statements.get(FIND_SESSIONS))
                    .bind("limit", limit)
                    .bind("offset", offset)
                    .map(toSession())
                    .list();

                for (Session session : sessions) {
                    UUID sessionId = session.getSessionId();

                    handle.createQuery(statements.get(FIND_PLAYER_SESSIONS_BY_ID))
                        .bind("session_id", session.getSessionId().toString())
                        .map(toPlayerStats(sessionId))
                        .forEach(stats -> session.setPlayerStats(stats.playerId, stats));

                    target.save(session);
                }

                if (sessions.size() < limit) {
                    break;
                }

                offset += limit;
            }
        });
    }

    private static RowMapper<Session> toSession() {
        return (r, ctx) -> {
            UUID sessionId = UUID.fromString(r.getString("session_id"));
            String arenaSlug = r.getString("arena_slug");
            Session session = new Session(sessionId, arenaSlug);

            SessionStats stats = session.getSessionStats();
            stats.startTime = r.getTimestamp("start_time").toInstant();
            stats.endTime = r.getTimestamp("end_time").toInstant();
            stats.lastWave = r.getInt("last_wave");
            stats.conclusion = SessionConclusion.valueOf(r.getString("conclusion"));

            return session;
        };
    }

    private static RowMapper<PlayerSessionStats> toPlayerStats(UUID sessionId) {
        return (r, ctx) -> {
            UUID playerId = UUID.fromString(r.getString("player_id"));
            String playerName = r.getString("player_name");

            PlayerSessionStats stats = new PlayerSessionStats(sessionId, playerId, playerName);
            stats.className = r.getString("class");
            stats.joinTime = safe(r.getTimestamp("join_time"), Timestamp::toInstant);
            stats.readyTime = safe(r.getTimestamp("ready_time"), Timestamp::toInstant);
            stats.leaveTime = safe(r.getTimestamp("leave_time"), Timestamp::toInstant);
            stats.deathTime = safe(r.getTimestamp("death_time"), Timestamp::toInstant);
            stats.kills = r.getInt("kills");
            stats.dmgDone = r.getInt("dmg_done");
            stats.dmgTaken = r.getInt("dmg_taken");
            stats.swings = r.getInt("swings");
            stats.hits = r.getInt("hits");
            stats.lastWave = r.getInt("last_wave");
            stats.conclusion = safe(r.getString("conclusion"), PlayerConclusion::valueOf, PlayerConclusion.DEFEAT);

            return stats;
        };
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

    public static JdbcStatsStore create(
        ConfigurationSection config,
        MobArenaStats plugin
    ) throws Exception {
        String type = config.getString("type");
        String url = config.getString("url");
        String username = config.getString("username");
        String password = config.getString("password");
        Jdbi jdbi = Jdbi.create(url, username, password);

        // Load up migrations and statements for the given type
        ResourceLoader loader = ResourceLoader.create(plugin.getClass().getClassLoader());
        Migrations migrations = Migrations.create(loader, type);
        Statements statements = Statements.create(loader, type);
        Logger log = plugin.getLogger();

        // Bring database schema up to speed
        SchemaMigrator migrator = new SchemaMigrator(jdbi, migrations, statements, log);
        migrator.migrate();

        return new JdbcStatsStore(jdbi, statements);
    }

}
