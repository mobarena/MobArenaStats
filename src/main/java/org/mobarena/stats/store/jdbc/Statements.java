package org.mobarena.stats.store.jdbc;

import org.mobarena.stats.util.ResourceLoader;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

class Statements {

    private final Map<Statement, String> sql;

    private Statements(Map<Statement, String> sql) {
        this.sql = sql;
    }

    String get(Statement statement) {
        return sql.get(statement);
    }

    static Statements create(ResourceLoader loader, String type) throws IOException {
        EnumMap<Statement, String> result = new EnumMap<>(Statement.class);
        for (Statement statement : Statement.values()) {
            // SCREAMING_SNAKE_CASE -> kebab-case, .sql file extension
            String filename = statement.toString().toLowerCase().replace('_', '-') + ".sql";
            String sql = loader.loadString(type + "/" + filename);
            result.put(statement, sql);
        }
        return new Statements(result);
    }

}
