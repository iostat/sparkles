package io.stat.sparkles;

import java.net.URL;
import java.util.NoSuchElementException;

/**
 * Created by io on 3/11/16. io is an asshole because
 * he doesn't write documentation for his code.
 *
 * @author Ilya Ostrovskiy (https://github.com/iostat/)
 */
final class DependencyResolution {
    enum Confidence {
        NONE,
        WEAK,
        STRONG
    };

    private final URL url;
    private final Confidence confidence;

    DependencyResolution(URL url) { this(url, Confidence.STRONG); }
    DependencyResolution(URL url, Confidence confidence) {
        this.url        = url;

        if(url == null) {
            this.confidence = Confidence.NONE;
        } else {
            this.confidence = confidence;
        }
    }

    public URL getURL() {
        if(url == null) {
            throw new NoSuchElementException("No URL was set!");
        }

        return url;
    }

    public Confidence getConfidence() {
        return confidence;
    }

    public boolean isPresent() {
        return url != null;
    }

    static DependencyResolution strong(URL u) {
        return new DependencyResolution(u);
    }

    static DependencyResolution weak(URL u) {
        return new DependencyResolution(u, Confidence.WEAK);
    }

    static DependencyResolution notFound() {
        return new DependencyResolution(null, Confidence.NONE);
    }
}
