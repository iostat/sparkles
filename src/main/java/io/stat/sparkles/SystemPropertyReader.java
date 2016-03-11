package io.stat.sparkles;

import java.io.PrintStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

/**
 * Created by io on 3/11/16. io is an asshole because
 * he doesn't write documentation for his code.
 *
 * @author Ilya Ostrovskiy (https://github.com/iostat/)
 */
final class SystemPropertyReader {
    static List<MavenRepository> readRepositories() {
        String source = System.getProperty("sparkles.repositories", "");

        if(source.isEmpty()) {
            return emptyList();
        } else {
            String[] splitted = source.split(";");
            List<MavenRepository> mapped = new ArrayList<>(splitted.length);
            for(String elem : splitted) {
                Logger.info("Registered %s", elem);

                try {
                    mapped.add(new MavenRepository(elem));
                } catch (MalformedURLException e) {
                    e.printStackTrace(System.err);
                }
            }
            return unmodifiableList(mapped);
        }
    }

    static List<MavenCoordinates> readArtifacts() {
        String source = System.getProperty("sparkles.artifacts", "");

        if(source.isEmpty()) {
            return emptyList();
        } else {
            String[] splitted = source.split(",");
            List<MavenCoordinates> mapped = new ArrayList<>(splitted.length);
            for(String elem : splitted) {
                Logger.info("Registered %s", elem);
                mapped.add(new MavenCoordinates(elem));
            }
            return unmodifiableList(mapped);
        }
    }

    static Logger.Verbosity readVerbosity() {
        return Logger.Verbosity.valueOf(System.getProperty("sparkles.verbosity", "WARN"));
    }

    static PrintStream readLogTarget() {
        String setting = System.getProperty("sparkles.log", "stderr").toLowerCase();

        switch(setting) {
            case "stdout": return System.out;
            case "stderr": return System.err;
            default:       throw new IllegalArgumentException("only \"stdout\" and \"stderr\" are currently supported");
        }
    }
}
