package org.mobarena.stats.store.jdbc;

import org.junit.jupiter.api.Test;
import org.mobarena.stats.util.ResourceLoader;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

/**
 * This very simple test just ensures that all of the supported JDBC-based
 * store types have all the necessary SQL statement files. The actual call to
 * {@link Statements#create(ResourceLoader, String)} will throw an exception
 * if a file is missing, but the unit test ensures that it has content.
 * <p>
 * The tight coupling with {@link org.mobarena.stats.util.ResourceLoader} is
 * not as daunting as it may seem, since {@link Statements} itself is a hard
 * bootstrap-only utility class, and its usage is carefully wrapped in other
 * bootstrap components.
 */
class StatementsTest {

    @Test
    void sqlite() throws Exception {
        test("sqlite");
    }

    @Test
    void mysql() throws Exception {
        test("mysql");
    }

    private void test(String type) throws Exception {
        ResourceLoader loader = ResourceLoader.create(Statements.class.getClassLoader());
        Statements statements = Statements.create(loader, type);
        for (Statement statement : Statement.values()) {
            String sql = statements.get(statement);
            assertThat(sql, notNullValue());
        }
    }

}
