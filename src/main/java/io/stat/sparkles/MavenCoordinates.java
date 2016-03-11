package io.stat.sparkles;

import java.util.Optional;

/**
 * Created by io on 3/11/16. io is an asshole because
 * he doesn't write documentation for his code.
 *
 * @author Ilya Ostrovskiy (https://github.com/iostat/)
 */
final class MavenCoordinates {
    private final String group;
    private final String name;
    private final String version;
    private final String classifier;
    private final String extension;

    public String           getGroup()      { return group;                           }
    public String           getName()       { return name;                            }
    public String           getVersion()    { return version;                         }
    public String           getExtension()  { return extension;                       }
    public Optional<String> getClassifier() { return Optional.ofNullable(classifier); }

    MavenCoordinates(String coordinates) {
        String[] splits = coordinates.split(":");

        if(splits.length < 3 || splits.length > 5) {
            throw new IllegalArgumentException("coordinates must be group:name:version[:classifier[:extension]]");
        }

        String maybeClassifier = splits.length == 4 ? splits[3] : "";
        this.group      = splits[0];
        this.name       = splits[1];
        this.version    = splits[2];
        this.classifier = maybeClassifier.isEmpty() ? null : maybeClassifier;
        this.extension = splits.length == 5 ? splits[4] : "jar";

        Logger.debug("Parsed %s", toString());
    }

    MavenCoordinates(String group, String name, String version) {
        this(group, name, version, null);
    }

    MavenCoordinates(String group, String name, String version, String classifier) {
        this(group, name, version, classifier, null);
    }

    MavenCoordinates(String group, String name, String version, String classifier, String extension) {
        this.group      = group;
        this.name       = name;
        this.version    = version;
        this.classifier = classifier;
        this.extension  = extension;
    }

    public String getCoordinatePath() {
        StringBuilder sb = new StringBuilder("/");

        for(String groupFragment : group.split("\\.")) {
            sb.append(groupFragment).append('/');
        }

        sb.append(name).append('/')
          .append(version).append('/')
          .append(name).append('-').append(version);

        if(classifier != null) {
            sb.append('-').append(classifier);
        }

        sb.append('.').append(extension);

        return sb.toString();
    }

    @Override public String toString() {
        return String.format(
                "MavenCoordinates(%s:%s:%s:%s:%s)",
                group,
                name,
                version,
                classifier == null ? "" : classifier,
                extension
        );
    }
}
