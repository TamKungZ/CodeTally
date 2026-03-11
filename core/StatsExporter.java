package me.tamkungz.codetally;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

/** Writes {@link SourceStats} to machine-readable files for CI pipelines. */
public class StatsExporter {

    public static void write(SourceStats stats,
                             File output,
                             String format,
                             boolean skipBlanks,
                             boolean skipComments) throws Exception {
        if (output == null) return;

        File parent = output.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        String f = (format == null || format.isBlank()) ? "json" : format.trim().toLowerCase();
        String content = switch (f) {
            case "csv" -> toCsv(stats);
            case "json" -> toJson(stats, skipBlanks, skipComments);
            default -> throw new IllegalArgumentException("Unsupported report format: " + format + " (use json/csv)");
        };

        Files.writeString(output.toPath(), content, StandardCharsets.UTF_8);
    }

    private static String toCsv(SourceStats stats) {
        StringBuilder sb = new StringBuilder();
        sb.append("ext,files,lines,chars,blank,comment\n");
        for (Map.Entry<String, long[]> e : stats.getByExtension().entrySet()) {
            long[] v = e.getValue();
            sb.append(escapeCsv(e.getKey())).append(',')
                    .append(v[0]).append(',')
                    .append(v[1]).append(',')
                    .append(v[2]).append(',')
                    .append(v[3]).append(',')
                    .append(v[4]).append('\n');
        }
        sb.append("TOTAL,")
                .append(stats.getTotalFiles()).append(',')
                .append(stats.getTotalLines()).append(',')
                .append(stats.getTotalChars()).append(',')
                .append(stats.getTotalBlanks()).append(',')
                .append(stats.getTotalComments()).append('\n');

        if (!stats.getAuthorLines().isEmpty()) {
            sb.append("\nauthor,lines\n");
            for (Map.Entry<String, Long> e : stats.getAuthorLines().entrySet()) {
                sb.append(escapeCsv(e.getKey())).append(',').append(e.getValue()).append('\n');
            }
        }
        return sb.toString();
    }

    private static String toJson(SourceStats stats, boolean skipBlanks, boolean skipComments) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"totals\": {\n");
        sb.append("    \"files\": ").append(stats.getTotalFiles()).append(",\n");
        sb.append("    \"lines\": ").append(stats.getTotalLines()).append(",\n");
        sb.append("    \"chars\": ").append(stats.getTotalChars()).append(",\n");
        sb.append("    \"blank\": ").append(stats.getTotalBlanks()).append(",\n");
        sb.append("    \"comment\": ").append(stats.getTotalComments()).append("\n");
        sb.append("  },\n");
        sb.append("  \"options\": {\n");
        sb.append("    \"skipBlankLines\": ").append(skipBlanks).append(",\n");
        sb.append("    \"skipCommentLines\": ").append(skipComments).append("\n");
        sb.append("  },\n");
        sb.append("  \"byExtension\": [\n");

        int i = 0;
        for (Map.Entry<String, long[]> e : stats.getByExtension().entrySet()) {
            long[] v = e.getValue();
            sb.append("    {\"ext\": \"").append(escapeJson(e.getKey())).append("\", \"files\": ").append(v[0])
                    .append(", \"lines\": ").append(v[1])
                    .append(", \"chars\": ").append(v[2])
                    .append(", \"blank\": ").append(v[3])
                    .append(", \"comment\": ").append(v[4]).append("}");
            if (i++ < stats.getByExtension().size() - 1) sb.append(',');
            sb.append('\n');
        }
        sb.append("  ],\n");
        sb.append("  \"authors\": [\n");
        int j = 0;
        for (Map.Entry<String, Long> e : stats.getAuthorLines().entrySet()) {
            sb.append("    {\"author\": \"").append(escapeJson(e.getKey())).append("\", \"lines\": ").append(e.getValue()).append("}");
            if (j++ < stats.getAuthorLines().size() - 1) sb.append(',');
            sb.append('\n');
        }
        sb.append("  ]\n");
        sb.append("}\n");
        return sb.toString();
    }

    private static String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static String escapeCsv(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r")) {
            return '"' + s.replace("\"", "\"\"") + '"';
        }
        return s;
    }
}

