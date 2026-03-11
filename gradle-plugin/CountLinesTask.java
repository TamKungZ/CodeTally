package me.tamkungz.codetally;

import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;

import java.io.File;

public abstract class CountLinesTask extends DefaultTask {

    @Input @Optional public abstract Property<Boolean> getSkipBlankLines();
    @Input @Optional public abstract Property<Boolean> getSkipCommentLines();
    @Input @Optional public abstract Property<Boolean> getVerbose();

    @TaskAction
    public void count() throws Exception {
        boolean skipBlanks   = getSkipBlankLines().getOrElse(false);
        boolean skipComments = getSkipCommentLines().getOrElse(false);
        boolean verbose      = getVerbose().getOrElse(false);

        File src = new File(getProject().getProjectDir(), "src");

        SourceAnalyzer analyzer = new SourceAnalyzer(skipBlanks, skipComments);
        SourceStats stats = analyzer.analyze(src, System.out::println, verbose);

        StatsReporter.print(stats, skipBlanks, skipComments, System.out::println);
    }
}