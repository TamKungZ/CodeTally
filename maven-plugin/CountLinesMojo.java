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

            StatsReporter.print(stats, skipBlankLines, skipCommentLines, log);

        } catch (Exception e) {
            throw new MojoExecutionException("CodeTally failed: " + e.getMessage(), e);
        }
    }
}
