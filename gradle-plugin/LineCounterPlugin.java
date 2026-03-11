package me.tamkungz.codetally;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class LineCounterPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getTasks().register("countLines", CountLinesTask.class, task -> {
            task.getSourceFiles().from(project.fileTree(new java.io.File(project.getProjectDir(), "src")));
            task.getOutputFile().convention(project.getLayout().getBuildDirectory().file("reports/codetally/codetally-report.json"));
            task.getReportFormat().convention("json");
            task.getMaxLines().convention(0L);
            task.getGitBlame().convention(false);
            task.getSkipBlankLines().convention(false);
            task.getSkipCommentLines().convention(false);
            task.getVerbose().convention(false);
            task.setGroup("reporting");
            task.setDescription("Count lines, characters, and blank/comment lines across source files.");
        });
    }
}
