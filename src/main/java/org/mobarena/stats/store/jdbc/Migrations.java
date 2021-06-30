package org.mobarena.stats.store.jdbc;

import org.mobarena.stats.util.ResourceLoader;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

class Migrations {

    private final ResourceLoader loader;
    private final String type;

    private Migrations(ResourceLoader loader, String type) {
        this.loader = loader;
        this.type = type;
    }

    List<String> list() throws URISyntaxException, IOException {
        return loader.list(type + "/migration");
    }

    String get(String filename) throws IOException {
        return loader.loadString(type + "/migration/" + filename);
    }

    static Migrations create(ResourceLoader loader, String type) {
        return new Migrations(loader, type);
    }

}
