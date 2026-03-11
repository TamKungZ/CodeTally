package me.tamkungz.codetally;

import java.util.Map;
import java.util.function.Consumer;

/** Renders a {@link SourceStats} summary to any string-consumer logger. */
public class StatsReporter {

    public static void print(SourceStats s, boolean skipBlanks, boolean skipComments,
                             Consumer<String> log) {
        log.accept("");
        log.accept("╔══════════════════════════════════════════════════════╗");
        log.accept("║               CodeTally – Statistics                 ║");
        log.accept("╠══════════╦════════╦════════════╦════════════╦════════╣");
        log.accept(String.format("║ %-8s ║ %6s ║ %10s ║ %10s ║ %6s ║",
                "Ext", "Files", "Lines", "Chars", "Blank"));
        log.accept("╠══════════╬════════╬════════════╬════════════╬════════╣");

        for (Map.Entry<String, long[]> e : s.getByExtension().entrySet()) {
            long[] v = e.getValue();
            log.accept(String.format("║ %-8s ║ %6d ║ %10d ║ %10d ║ %6d ║",
                    e.getKey(), v[0], v[1], v[2], v[3]));
        }

        log.accept("╠══════════╬════════╬════════════╬════════════╬════════╣");
        log.accept(String.format("║ %-8s ║ %6d ║ %10d ║ %10d ║ %6d ║",
                "TOTAL", s.getTotalFiles(), s.getTotalLines(),
                s.getTotalChars(), s.getTotalBlanks()));
        log.accept("╚══════════╩════════╩════════════╩════════════╩════════╝");

        if (skipBlanks || skipComments) {
            log.accept("  (excluded from counts — "
                    + (skipBlanks   ? "blank=" + s.getTotalBlanks() + " " : "")
                    + (skipComments ? "comment=" + s.getTotalComments()    : "")
                    + ")");
        }
        log.accept("");
    }
}