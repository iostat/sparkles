package io.stat.sparkles;

import java.io.PrintStream;

/**
 * Created by io on 3/11/16. io is an asshole because
 * he doesn't write documentation for his code.
 *
 * @author Ilya Ostrovskiy (https://github.com/iostat/)
 */
final class Logger {
    enum Verbosity {
        DEBUG(1000),
        INFO (500),
        WARN (100),
        ERROR(0);

        private final int level;

        Verbosity(int level) {
            this.level = level;
        }

        // e.g., error == 0, and if the target is INFO
        // errors will get logged.
        boolean shouldLog(Verbosity target) {
            return this.level >= target.level;
        }
    }

    private static final Object[]    EMPTY_OBJECT_ARRAY = new Object[0];
    private static final Verbosity   verbosity          = SystemPropertyReader.readVerbosity();
    private static final PrintStream output             = SystemPropertyReader.readLogTarget();
    private static final byte[]      $outputLock        = new byte[0];


    static PrintStream getPrintStream() {
        return output;
    }

    static void log(Verbosity level, String message) { log(level, message, EMPTY_OBJECT_ARRAY); }
    static void log(Verbosity level, String format, Object... args) {
        if(verbosity.shouldLog(level)) {
            synchronized ($outputLock) {
                output.println(String.format(format, args));
            }
        }
    }

    static void debug(String message) { log(Verbosity.DEBUG, message); }
    static void info(String message)  { log(Verbosity.INFO,  message); }
    static void warn(String message)  { log(Verbosity.WARN,  message); }
    static void error(String message) { log(Verbosity.ERROR, message); }
    static void debug(String format, Object... args) { log(Verbosity.DEBUG, format, args); }
    static void info(String format,  Object... args) { log(Verbosity.INFO , format, args); }
    static void warn(String format,  Object... args) { log(Verbosity.WARN , format, args); }
    static void error(String format, Object... args) { log(Verbosity.ERROR, format, args); }
}
