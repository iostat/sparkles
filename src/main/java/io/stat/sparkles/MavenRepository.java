package io.stat.sparkles;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by io on 3/11/16. io is an asshole because
 * he doesn't write documentation for his code.
 *
 * @author Ilya Ostrovskiy (https://github.com/iostat/)
 */
final class MavenRepository {
    public static final int MAX_DEPTH = 10; // todo: make this tunable

    private final String name;
    private final URL    root;

    public URL    getRoot() { return root; }
    public String getName() { return name; }

    MavenRepository(String compact) throws MalformedURLException {
        int firstComma = compact.indexOf(',');

        if (firstComma == -1) {
            throw new IllegalArgumentException("Compact representation must be in the form strong <name>,<url>");
        }

        this.name = compact.substring(0, firstComma);
        this.root = new URL(compact.substring(firstComma + 1));
    }

    MavenRepository(String name, URL root) {
        this.name = name;
        this.root = root;
    }

    DependencyResolution resolveArtifact(MavenCoordinates c) {
        try {
            URL fullArtifactPath = new URL(root.toExternalForm() + c.getCoordinatePath());

            URLConnection connection = fullArtifactPath.openConnection();
            try {
                HttpURLConnection asHTTPConnection = ((HttpURLConnection)connection);
                return followHTTPRedirects(asHTTPConnection, 0);
            } catch(ClassCastException e) {
                // it's some other protocol that we dont support/care about
                // let's just assume the URL is valid
                Logger.error("ClassCastException on %s to HttpURLConnection!", fullArtifactPath);
                return DependencyResolution.weak(fullArtifactPath);
            }
        } catch (IOException e) {
            Logger.error("IOException on a URL for %s::%s: %s!", getName(), c, e.getMessage());
            // fallthrough to return Optional.empty();
        }

        Logger.warn("Could not resolve %s", c);
        return DependencyResolution.notFound();
    }

    private void applySettingsToConnection(HttpURLConnection connection) throws ProtocolException {
        connection.setReadTimeout(30000);
        connection.setRequestMethod("HEAD");
        connection.setRequestProperty("User-Agent", "Sparkles Classloader 0.1");
    }

    private DependencyResolution followHTTPRedirects(HttpURLConnection connection, int depth) throws IOException {
        if(depth == MAX_DEPTH) {
            return DependencyResolution.notFound();
        }

        Logger.debug("Trying %s", connection.getURL());

        applySettingsToConnection(connection);

        int response = connection.getResponseCode();
        if(response == HttpURLConnection.HTTP_OK) {
            return DependencyResolution.strong(connection.getURL());
        } else if (response == HttpURLConnection.HTTP_MOVED_TEMP ||
                   response == HttpURLConnection.HTTP_MOVED_PERM ||
                   response == HttpURLConnection.HTTP_SEE_OTHER) {

            String cookie = connection.getHeaderField("Cookie");

            URL newURL;
            try {
                newURL = new URL(connection.getHeaderField("Location"));
            } catch(MalformedURLException e) {
                return DependencyResolution.notFound();
            }

            try {
                HttpURLConnection asHTTPConnection = ((HttpURLConnection)newURL.openConnection());

                asHTTPConnection.setRequestMethod("HEAD");
                if(cookie != null) {
                    asHTTPConnection.setRequestProperty("Cookie", cookie);
                }

                return followHTTPRedirects(connection, depth + 1);
            } catch(ClassCastException e) {
                Logger.warn("Cannot confidently resolve non-HTTP[S] URL %s", connection.getURL());
                return DependencyResolution.weak(newURL);
            } catch(IOException e) {
                Logger.error("Could not open connection to %s", connection.getURL());
                return DependencyResolution.notFound();
            }
        } else {
            Logger.warn("Could not resolve at %s (HTTP %d)", connection.getURL(), response);
            return DependencyResolution.notFound();
        }
    }
}
