package org.mobarena.stats.store.jdbc;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.Batch;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.mobarena.stats.store.jdbc.Statement.FIND_ALL_MIGRATIONS;
import static org.mobarena.stats.store.jdbc.Statement.INSERT_MIGRATION;

class SchemaMigrator {

    private final Jdbi jdbi;
    private final Migrations migrations;
    private final Statements statements;
    private final Logger log;

    SchemaMigrator(
        Jdbi jdbi,
        Migrations migrations,
        Statements statements,
        Logger log
    ) {
        this.jdbi = jdbi;
        this.migrations = migrations;
        this.statements = statements;
        this.log = log;
    }

    void migrate() throws IOException, SQLException, URISyntaxException {
        List<String> filenames = migrations.list();
        List<String> completed = getCompletedMigrations();

        filenames.removeAll(completed);

        if (filenames.isEmpty()) {
            log.info("Schema is up to date.");
            return;
        }

        if (completed.isEmpty()) {
            log.info("Schema is has not yet been initialized, migrating...");
        } else {
            log.info("Schema is " + filenames.size() + " version(s) behind, migrating...");
        }

        for (String filename : filenames) {
            execute(filename);
        }

        log.info("Schema migration complete.");
    }

    private List<String> getCompletedMigrations() throws SQLException {
        return jdbi.withHandle(handle -> {
            // We don't really have a good way to check if the database has
            // migration info without making some actual queries, which will
            // fail if it doesn't. Instead, we can use the database metadata
            // (available via the underlying JDBC connection object) to find
            // out if the schema migrations table exists.
            Connection connection = handle.getConnection();
            String catalog = connection.getCatalog();
            String schema = connection.getSchema();
            DatabaseMetaData meta = connection.getMetaData();
            try (ResultSet tables = meta.getTables(catalog, schema, "schema_migrations", null)) {
                while (tables.next()) {
                    String name = tables.getString("TABLE_NAME");
                    if (name.equals("schema_migrations")) {
                        // Jackpot! We found the table, now query it.
                        String sql = statements.get(FIND_ALL_MIGRATIONS);
                        return handle.createQuery(sql)
                            .map((r, ctx) -> r.getString("filename"))
                            .list();
                    }
                }
            }

            // No migrations table means fresh database.
            return Collections.emptyList();
        });
    }

    private void execute(String filename) throws IOException {
        String content = migrations.get(filename);

        // Migration files may contain several different statements,
        // but not all databases support multiple statements in a
        // single update, so we split the file contents by semicolon
        // and hold our breath while we invoke each part...
        List<String> parts = Arrays.stream(content.split(";"))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toList());

        jdbi.useTransaction(handle -> {
            Instant executed = Instant.now();
            try {
                if (parts.size() == 1) {
                    handle.execute(parts.get(0));
                } else {
                    Batch batch = handle.createBatch();
                    parts.forEach(batch::add);
                    batch.execute();
                }

                String sql = statements.get(INSERT_MIGRATION);
                handle.createUpdate(sql)
                    .bind("filename", filename)
                    .bind("executed", executed)
                    .bind("success", true)
                    .bind("error", (String) null)
                    .execute();

                log.info("\u2713 " + filename);
            } catch (Exception e) {
                String sql = statements.get(INSERT_MIGRATION);
                handle.createUpdate(sql)
                    .bind("filename", filename)
                    .bind("executed", executed)
                    .bind("success", false)
                    .bind("error", e.getMessage())
                    .execute();

                log.severe("\u2717 " + filename);
                throw new IllegalStateException("Migration failed: " + filename, e);
            }
        });
    }

}
