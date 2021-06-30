package org.mobarena.stats.util;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

/**
 * Resource loading is no joke. In IDEs and build tools, resources
 * typically sit in a file system folder like src/main/resources,
 * which means the URI scheme is "file:". When a plugin is deployed
 * to a Minecraft server, however, the URI scheme changes to "jar:",
 * which means any operation that is scheme-dependent will have to
 * support both schemes for a good developer experience...
 * <p>
 * Loading a specific resource is scheme-independent, but iterating
 * resources isn't. The resource loader provides a "list" method to
 * list all the resources under a given prefix path, which means it
 * has to iterate (part of) the classpath, so it has to know which
 * scheme it's working under.
 * <p>
 * Thus, to properly unit test the resource loader's jar-specific
 * code path, we need to somehow provide a class loader that will
 * resolve resources with the jar URI scheme.
 * <p>
 * As it turns out, here be dragons...
 */
class ResourceLoaderTest {

    @Test
    void listResourcesInDirectory() throws Exception {
        // The normal class loader from the test class will properly
        // resolve the resources in src/test/resources because this
        // folder is part of the class path during test runs, so we
        // don't have to do anything special here.
        ClassLoader loader = getClass().getClassLoader();
        ResourceLoader subject = new ResourceLoader(loader);

        List<String> result = subject.list("dummy/migration");

        List<String> expected = Arrays.asList(
            "V1__baseline.sql",
            "V2__new_stuff.sql",
            "V3__changed_stuff.sql"
        );
        assertThat(result, equalTo(expected));
    }

    @Test
    void loadResourceInDirectory() throws Exception {
        ClassLoader loader = getClass().getClassLoader();
        ResourceLoader subject = new ResourceLoader(loader);
        String name = "dummy/migration/V1__baseline.sql";

        String result = subject.loadString(name);

        String expected = String.join(
            "\n",
            "-- Some database baseline",
            "CREATE TABLE IF NOT EXISTS bob(id  INTEGER PRIMARY KEY AUTOINCREMENT);",
            ""
        );
        assertThat(result, equalTo(expected));
    }

    @Test
    void listResourcesInJarFile() throws Exception {
        // For the jar case, we wrap the test resources in a real,
        // and temporary, jar-file. This is extremely complex stuff
        // for a unit test, but it does mean we get a test case that
        // hits that specific code path for a boost of confidence.
        Path jar = createJarWithTestResources();
        try {
            // We also need a special class loader that can access
            // the contents of the jar-file with the correct scheme,
            // and while this isn't as complex, the URL/URI stuff is
            // pretty intricate.
            ClassLoader loader = createJarClassLoader(jar);
            ResourceLoader subject = new ResourceLoader(loader);

            List<String> result = subject.list("dummy/migration");

            List<String> expected = Arrays.asList(
                "V1__baseline.sql",
                "V2__new_stuff.sql",
                "V3__changed_stuff.sql"
            );
            assertThat(result, equalTo(expected));
        } finally {
            Files.deleteIfExists(jar);
        }
    }

    @Test
    void loadResourceInJarFile() throws Exception {
        Path jar = createJarWithTestResources();
        try {
            ClassLoader loader = createJarClassLoader(jar);
            ResourceLoader subject = new ResourceLoader(loader);
            String name = "dummy/migration/V1__baseline.sql";

            String result = subject.loadString(name);

            String expected = String.join(
                "\n",
                "-- Some database baseline",
                "CREATE TABLE IF NOT EXISTS bob(id  INTEGER PRIMARY KEY AUTOINCREMENT);",
                ""
            );
            assertThat(result, equalTo(expected));
        } finally {
            Files.deleteIfExists(jar);
        }
    }

    private static Path createJarWithTestResources() throws Exception {
        // To create a jar file, we write some bytes to a jar output
        // stream, along with some jar-specific convenience functions
        // related to the concept of "entries". The implementation is
        // an iterative version of this solution from StackOverflow:
        //
        //   https://stackoverflow.com/a/59351837/2221849
        //
        // Each file (and folder) in the test resources folder needs
        // to be written to the jar file as an "entry". Directories
        // are just empty entries that end in a forward slash (/),
        // while files are names and some actual bytes.
        //
        // We have to "relativize" the paths before writing the names
        // down, because the "root" starts in src/test/resources, and
        // we want to strip that part out of the names in the actual
        // jar file.
        Path jar = Files.createTempFile("mobarena-stats_", ".jar");
        try {
            JarOutputStream target = new JarOutputStream(new FileOutputStream(jar.toFile()));
            File root = Paths.get("src", "test", "resources").toFile();
            Deque<File> queue = new ArrayDeque<>();
            queue.push(root);
            while (!queue.isEmpty()) {
                File file = queue.pop();
                File relative = root.toPath().relativize(file.toPath()).toFile();
                String name = relative.getPath().replace("\\", "/");
                if (file.isDirectory()) {
                    if (!name.isEmpty()) {
                        JarEntry entry = new JarEntry(name + "/");
                        entry.setTime(file.lastModified());
                        target.putNextEntry(entry);
                        target.closeEntry();
                    }
                    File[] children = file.listFiles();
                    if (children != null) {
                        for (File child : children) {
                            queue.push(child);
                        }
                    }
                } else {
                    JarEntry entry = new JarEntry(name);
                    entry.setTime(file.lastModified());
                    target.putNextEntry(entry);
                    try (InputStream is = new FileInputStream(file)) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = is.read(buffer)) != -1) {
                            target.write(buffer, 0, length);
                        }
                        target.closeEntry();
                    }
                }
            }
            target.close();
        } catch (Exception up) {
            Files.deleteIfExists(jar);
            throw up;
        }

        return jar;
    }

    private static ClassLoader createJarClassLoader(Path jar) throws Exception {
        // I'll be honest and admit that I have no clue about the
        // structure of these URLs, but it turns out the the "!/"
        // suffix is of utmost importance.
        //
        // My guess is that it's necessary because the jar-scheme
        // "wraps" the file-scheme, and so it needs a dedicated
        // separator to get the following format:
        //
        //   jar:file:<path-on-file-system>!/<path-in-jar-file>
        //
        // That is, the "!/" is there to indicate the end of the
        // file system path and the start of the jar-file path.
        String file = jar.toUri().toURL() + "!/";
        URL url = new URL("jar", "", file);
        return new URLClassLoader(new URL[]{url}, null);
    }

}
