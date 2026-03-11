package me.tamkungz.codetally;

import java.io.File;
import java.util.Map;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.TaskAction;

public abstract class CountLinesTask extends DefaultTask {

    @InputFiles
    @PathSensitive(PathSensitivity.RELATIVE)
    public abstract ConfigurableFileCollection getSourceFiles();

    @OutputFile @Optional
    public abstract RegularFileProperty getOutputFile();

    @Input @Optional public abstract Property<String> getReportFormat();
    @Input @Optional public abstract Property<Long> getMaxLines();
    @Input @Optional public abstract Property<Boolean> getGitBlame();
    @Input @Optional public abstract Property<Boolean> getSkipBlankLines();
    @Input @Optional public abstract Property<Boolean> getSkipCommentLines();
    @Input @Optional public abstract Property<Boolean> getVerbose();

    @TaskAction
    public void count() throws Exception {
        boolean skipBlanks   = getSkipBlankLines().getOrElse(false);
        boolean skipComments = getSkipCommentLines().getOrElse(false);
        boolean verbose      = getVerbose().getOrElse(false);
        boolean gitBlame     = getGitBlame().getOrElse(false);
        long maxLines        = getMaxLines().getOrElse(0L);
        String reportFormat  = getReportFormat().getOrElse("json");

        File src = new File(getProject().getProjectDir(), "src");
        if (!src.exists()) {
            getLogger().warn("CodeTally: src/ directory not found at " + src.getAbsolutePath());
            return;
        }

        SourceAnalyzer analyzer = new SourceAnalyzer(skipBlanks, skipComments);
        SourceStats stats = analyzer.analyze(src, System.out::println, verbose);

        if (gitBlame) {
            GitBlameAnalyzer blameAnalyzer = new GitBlameAnalyzer();
            for (File f : analyzer.collectAnalyzableFiles(src)) {
                for (Map.Entry<String, Long> e : blameAnalyzer.analyze(getProject().getProjectDir(), f).entrySet()) {
                    stats.addAuthorLines(e.getKey(), e.getValue());
                }
            }
        }

        StatsReporter.print(stats, skipBlanks, skipComments, System.out::println);

        File reportOutput = getOutputFile().isPresent() ? getOutputFile().get().getAsFile() : null;
        if (reportOutput != null) {
            StatsExporter.write(stats, reportOutput, reportFormat, skipBlanks, skipComments);
            getLogger().lifecycle("CodeTally report written to " + reportOutput.getAbsolutePath());
        }

        if (maxLines > 0 && stats.getTotalLines() > maxLines) {
            throw new GradleException("CodeTally threshold failed: total lines " + stats.getTotalLines()
                    + " exceeds maxLines=" + maxLines);
        }
    }
}
