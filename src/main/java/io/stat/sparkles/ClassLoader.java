package io.stat.sparkles;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by io on 3/11/16. io is an asshole because
 * he doesn't write documentation for his code.
 *
 * @author Ilya Ostrovskiy (https://github.com/iostat/)
 */
public class ClassLoader extends URLClassLoader {
    private java.lang.ClassLoader parent;
    private java.lang.ClassLoader delegate;
    private final byte[] $initLock;
    private boolean attempedToAddSparkles;
    private boolean finishedConstructing;


    public ClassLoader(java.lang.ClassLoader parent) {
        super(((URLClassLoader) parent).getURLs(), parent);
        this.$initLock              = new byte[0];
        this.attempedToAddSparkles  = false;
        this.parent = this.delegate = parent;

        try {
            URL[] urls = ArtifactJARResolver.resolveURLs(
                    SystemPropertyReader.readRepositories(),
                    SystemPropertyReader.readArtifacts()
            );

            for(URL u : urls) {
                addURL(u);
            }
        } catch(Exception e) {
            Logger.debug("Could not initialize Sparkles! Will delegate to default system classloader");
            e.printStackTrace(Logger.getPrintStream());
        } finally {
            attempedToAddSparkles = true;
        }
    }
}
