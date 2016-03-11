package io.stat.sparkles;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Resolves Maven artifacts to JAR URLs and sticks them into the classpath
 *
 * @author Ilya Ostrovskiy (https://github.com/iostat/)
 */
final class ArtifactJARResolver {
    private ArtifactJARResolver() { }

    static URL[] resolveURLs(List<MavenRepository> repositories, List<MavenCoordinates> coordinates) {
        ArrayList<URL> resolvedJARs = new ArrayList<>(coordinates.size());
        for(MavenCoordinates c : coordinates) {
            boolean wasResolved = false;
            for(MavenRepository r : repositories) {
                DependencyResolution resolvedURL = r.resolveArtifact(c);
                if(resolvedURL.isPresent()) {
                    wasResolved = true;
                    URL url = resolvedURL.getURL();
                    resolvedJARs.add(url);

                    if(resolvedURL.getConfidence() == DependencyResolution.Confidence.STRONG) {
                        Logger.info("Confidently resolved %s to %s", c, url);
                        break;
                    } else {
                        Logger.warn("Resolved %s to %s with weak confidence!", c, url);
                    }
                }
            }

            if(!wasResolved) {
                Logger.error("Could not resolve %s!", c);
            }
        }

        URL[] ret = new URL[resolvedJARs.size()];
        return resolvedJARs.toArray(ret);
    }
}
