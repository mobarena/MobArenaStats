package org.mobarena.stats.store.mariadb;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;
import org.mobarena.stats.store.mysql.MysqlStatsStore;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

class MariadbStatsStoreTest {

    @Test
    void getUrlDefaultValues() {
        ConfigurationSection config = new YamlConfiguration();

        String result = MariadbStatsStore.getUrl(config);

        String expected = "jdbc:mariadb://localhost:3306/mobarena_stats?useSSL=false";
        assertThat(result, equalTo(expected));
    }

    @Test
    void getUrlConstructsJdbcUrl() {
        ConfigurationSection config = new YamlConfiguration();
        config.set("host", "stats.example.com");
        config.set("port", 1337);
        config.set("database", "mastats");
        config.set("ssl", true);

        String result = MariadbStatsStore.getUrl(config);

        String expected = "jdbc:mariadb://stats.example.com:1337/mastats?useSSL=true";
        assertThat(result, equalTo(expected));
    }

}
