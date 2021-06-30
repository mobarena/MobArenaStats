package org.mobarena.stats.util;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Lists and loads resources via a given {@link ClassLoader}.
 * <p>
 * The primary goal of this class is to provide a developer-friendly
 * abstraction over Java's complex concept of "resources", allowing
 * client components to focus their efforts on their own context.
 * <p>
 * In general, we know what resources we're looking for, and we just
 * want to load them into memory and apply them where needed, but we
 * also want to "scan" resource "folders". The latter is fairly easy
 * in a file system context, but in a jar-file, while still somewhat
 * doable, becomes a nightmare to have to do again and again. That's
 * where this class comes in, as a mild wrapper around something that
 * can best be described as infuriating.
 */
public class ResourceLoader {

    private final ClassLoader loader;

    ResourceLoader(ClassLoader loader) {
        this.loader = loader;
    }

    /**
     * Find all resources that match the given path.
     *
     * @param prefix a resource "prefix" to filter resources by
     * @return a list of all resources that match the given prefix
     * @throws URISyntaxException if the given path isn't a valid URI
     * in the context of the class loader
     * @throws IOException if an I/O error occurs during traversal
     */
    public List<String> list(String prefix) throws URISyntaxException, IOException {
        URL url = loader.getResource(prefix);
        if (url == null) {
            throw new NoSuchElementException("No resources found at " + prefix);
        }

        URI uri = url.toURI();
        if (uri.getScheme().equals("jar")) {
            try (FileSystem fs = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
                Path path = fs.getPath(prefix);
                return walk(path);
            }
        } else {
            Path path = Paths.get(uri);
            return walk(path);
        }
    }

    private List<String> walk(Path path) throws IOException {
        // When we traverse the path, we want to skip the folder
        // denoted by the path itself, and this is always first
        // in the stream.
        return Files.walk(path, 1)
            .skip(1)
            .map(Path::getFileName)
            .map(Path::toString)
            .sorted()
            .collect(Collectors.toList());
    }

    /**
     * Load the resource at the given path, interpreting its contents
     * as UTF-8 encoded text.
     *
     * @param path the path to the resource to load
     * @return the contents of the resource at the given path
     * @throws IOException if an I/O error occurs during loading
     */
    public String loadString(String path) throws IOException {
        InputStream is = loader.getResourceAsStream(path);
        if (is == null) {
            throw new FileNotFoundException("Resource not found: " + path);
        }

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) != -1) {
            output.write(buffer, 0, length);
        }

        return output.toString(StandardCharsets.UTF_8.name());
    }

    /**
     * Create a new resource loader with the given {@link ClassLoader}
     * as its source.
     *
     * @param loader a class loader to use as a source of resources
     * @return a new resource loader
     */
    public static ResourceLoader create(ClassLoader loader) {
        return new ResourceLoader(loader);
    }

}
