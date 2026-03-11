package me.tamkungz.codetally;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class LineCounterPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getTasks().register("countLines", CountLinesTask.class, task -> {
            task.getSkipBlankLines().convention(false);
            task.getSkipCommentLines().convention(false);
            task.getVerbose().convention(false);
            task.setGroup("reporting");
            task.setDescription("Count lines, characters, and blank/comment lines across source files.");
        });
    }
}