package me.tamkungz.codetally;

import java.util.Map;
import java.util.TreeMap;

/** Aggregated statistics produced by {@link SourceAnalyzer}. */
public class SourceStats {

    private long totalFiles;
    private long totalLines;
    private long totalChars;
    private long totalBlanks;
    private long totalComments;

    /** Per-extension breakdown: ext → long[]{files, lines, chars, blanks, comments} */
    private final Map<String, long[]> byExtension = new TreeMap<>();

    // ── Mutators used by SourceAnalyzer ─────────────────────────────────────

    public void addFile(String ext, long lines, long chars, long blanks, long comments) {
        totalFiles++;
        totalLines    += lines;
        totalChars    += chars;
        totalBlanks   += blanks;
        totalComments += comments;

        byExtension.merge(ext,
                new long[]{1, lines, chars, blanks, comments},
                (a, b) -> new long[]{
                        a[0]+b[0], a[1]+b[1], a[2]+b[2], a[3]+b[3], a[4]+b[4]
                });
    }

    // ── Accessors ────────────────────────────────────────────────────────────

    public long getTotalFiles()    { return totalFiles;    }
    public long getTotalLines()    { return totalLines;    }
    public long getTotalChars()    { return totalChars;    }
    public long getTotalBlanks()   { return totalBlanks;   }
    public long getTotalComments() { return totalComments; }
    public Map<String, long[]> getByExtension() { return byExtension; }
}