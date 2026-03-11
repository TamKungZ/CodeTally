package me.tamkungz.codetally;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

/** Collects per-author line ownership by executing git blame. */
public class GitBlameAnalyzer {

    public Map<String, Long> analyze(File repoDir, File file) {
        Map<String, Long> result = new TreeMap<>();
        if (repoDir == null || file == null) return result;

        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "git", "blame", "--line-porcelain", "--", file.getAbsolutePath()
            );
            pb.directory(repoDir);
            pb.redirectErrorStream(true);
            Process p = pb.start();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("author ")) {
                        String author = line.substring("author ".length()).trim();
                        if (!author.isEmpty()) {
                            result.merge(author, 1L, Long::sum);
                        }
                    }
                }
            }

            p.waitFor();
        } catch (Exception ignored) {
            // Optional feature: ignore errors when git is unavailable or file is not tracked.
        }

        return result;
    }
}

