package me.tamkungz.codetally;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

/**
 * Counts lines, characters, blank lines, and comment lines in source files.
 *
 * Usage:
 *   mvn codetally:count
 *
 * Or bind to a lifecycle phase in pom.xml:
 *   {@code <executions><execution><goals><goal>count</goal></goals></execution></executions>}
 */
@Mojo(name = "count", defaultPhase = LifecyclePhase.VERIFY, threadSafe = true)
public class CountLinesMojo extends AbstractMojo {

    /** Root directory of the Maven project. */
    @Parameter(defaultValue = "${project.basedir}", readonly = true)
    private File baseDir;

    /** Skip blank lines from the line/char count. */
    @Parameter(property = "codetally.skipBlankLines", defaultValue = "false")
    private boolean skipBlankLines;

    /** Skip single-line comment lines from the line/char count. */
    @Parameter(property = "codetally.skipCommentLines", defaultValue = "false")
    private boolean skipCommentLines;

    /** Print per-file statistics. */
    @Parameter(property = "codetally.verbose", defaultValue = "false")
    private boolean verbose;

    /** Optional output file path for machine-readable report (json/csv). */
    @Parameter(property = "codetally.outputFile")
    private File outputFile;

    /** Report format when outputFile is set: json or csv. */
    @Parameter(property = "codetally.reportFormat", defaultValue = "json")
    private String reportFormat;

    /** Fail build when total counted lines exceed this threshold. 0 disables check. */
    @Parameter(property = "codetally.maxLines", defaultValue = "0")
    private long maxLines;

    /** Enable git blame aggregation (author -> lines). */
    @Parameter(property = "codetally.gitBlame", defaultValue = "false")
    private boolean gitBlame;

    @Override
    public void execute() throws MojoExecutionException {
        File src = new File(baseDir, "src");
        if (!src.exists()) {
            getLog().warn("CodeTally: src/ directory not found at " + src.getAbsolutePath());
            return;
        }

        try {
            // Bridge Maven's log to a plain Consumer<String>
            java.util.function.Consumer<String> log = line -> getLog().info(line);

            SourceAnalyzer analyzer = new SourceAnalyzer(skipBlankLines, skipCommentLines);
            SourceStats stats = analyzer.analyze(src, log, verbose);

            if (gitBlame) {
                GitBlameAnalyzer blameAnalyzer = new GitBlameAnalyzer();
                for (File f : analyzer.collectAnalyzableFiles(src)) {
                    for (java.util.Map.Entry<String, Long> e : blameAnalyzer.analyze(baseDir, f).entrySet()) {
                        stats.addAuthorLines(e.getKey(), e.getValue());
                    }
                }
            }

            StatsReporter.print(stats, skipBlankLines, skipCommentLines, log);

            if (outputFile != null) {
                StatsExporter.write(stats, outputFile, reportFormat, skipBlankLines, skipCommentLines);
                getLog().info("CodeTally report written to " + outputFile.getAbsolutePath());
            }

            if (maxLines > 0 && stats.getTotalLines() > maxLines) {
                throw new MojoExecutionException("CodeTally threshold failed: total lines " + stats.getTotalLines()
                        + " exceeds maxLines=" + maxLines);
            }

        } catch (Exception e) {
            throw new MojoExecutionException("CodeTally failed: " + e.getMessage(), e);
        }
    }
}
