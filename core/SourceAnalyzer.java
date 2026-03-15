package me.tamkungz.codetally;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Language-agnostic source analyzer.
 * No Gradle or Maven dependencies — safe to share across both plugins.
 */
public class SourceAnalyzer {

    public static final Set<String> DEFAULT_EXTENSIONS = Set.of(
            ".java", ".kt", ".kts",
            ".js", ".ts", ".jsx", ".tsx",
            ".py", ".go", ".rs",
            ".json", ".xml", ".gradle"
    );

    private final boolean skipBlankLines;
    private final boolean skipCommentLines;

    public SourceAnalyzer(boolean skipBlankLines, boolean skipCommentLines) {
        this.skipBlankLines   = skipBlankLines;
        this.skipCommentLines = skipCommentLines;
    }

    /**
     * Walk {@code srcDir} recursively and return aggregated {@link SourceStats}.
     *
     * @param srcDir    root directory to scan (e.g. {@code src/})
     * @param logger    one-arg string consumer — use {@code System.out::println},
     *                  Gradle logger, or Maven log interchangeably
     * @param verbose   emit per-file output via {@code logger}
     */
    public SourceStats analyze(File srcDir, java.util.function.Consumer<String> logger, boolean verbose)
            throws Exception {
        List<File> files = collectAnalyzableFiles(srcDir);
        return analyze(srcDir, files, logger, verbose);
    }

    /**
     * Analyze a provided list of files. Useful when callers need to reuse the same file list
     * (e.g. for git blame) without scanning the directory tree twice.
     */
    public SourceStats analyze(File srcDir, List<File> files,
                               java.util.function.Consumer<String> logger, boolean verbose)
            throws Exception {

        SourceStats stats = new SourceStats();

        List<File> sortedFiles = new ArrayList<>(files);
        sortedFiles.sort(Comparator.comparing(File::getAbsolutePath));

        for (File file : sortedFiles) {
            String ext = extension(file.getName());

            List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);

            long fileLines = 0, fileChars = 0, fileBlanks = 0, fileComments = 0;
            boolean inBlockComment = false;

            for (String line : lines) {
                String trimmed = line.stripLeading();

                boolean isBlank   = trimmed.isEmpty();
                boolean isComment = false;

                if (!isBlank) {
                    if (inBlockComment) {
                        isComment = true;
                        if (trimmed.contains("*/")) {
                            inBlockComment = false;
                        }
                    } else if (trimmed.startsWith("//") || trimmed.startsWith("#")) {
                        isComment = true;
                    } else if (trimmed.startsWith("/*")) {
                        isComment = true;
                        if (!trimmed.contains("*/")) {
                            inBlockComment = true;
                        }
                    } else if (trimmed.startsWith("*/") || trimmed.startsWith("*")) {
                        isComment = true;
                        if (trimmed.startsWith("*/")) {
                            inBlockComment = false;
                        }
                    }
                }

                if (isBlank)   fileBlanks++;
                if (isComment) fileComments++;

                boolean count = true;
                if (skipBlankLines   && isBlank)   count = false;
                if (skipCommentLines && isComment)  count = false;

                if (count) {
                    fileLines++;
                    fileChars += line.length();
                }
            }

            stats.addFile(ext, fileLines, fileChars, fileBlanks, fileComments);

            if (verbose) {
                logger.accept(String.format("  %-60s  lines:%5d  chars:%7d",
                        srcDir.toURI().relativize(file.toURI()).getPath(),
                        fileLines, fileChars));
            }
        }

        return stats;
    }

    /** Collect files under srcDir filtered by {@link #DEFAULT_EXTENSIONS}. */
    public List<File> collectAnalyzableFiles(File srcDir) {
        List<File> files = collectFiles(srcDir);
        List<File> filtered = new ArrayList<>();
        for (File f : files) {
            if (DEFAULT_EXTENSIONS.contains(extension(f.getName()))) {
                filtered.add(f);
            }
        }
        return filtered;
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private List<File> collectFiles(File dir) {
        List<File> result = new ArrayList<>();
        if (dir == null || !dir.isDirectory()) return result;
        File[] children = dir.listFiles();
        if (children == null) return result;
        for (File f : children) {
            if (f.isDirectory()) result.addAll(collectFiles(f));
            else result.add(f);
        }
        return result;
    }

    private static String extension(String filename) {
        int dot = filename.lastIndexOf('.');
        return dot >= 0 ? filename.substring(dot) : "";
    }
}
