package edu.advising.report.html;

import edu.advising.report.ReportExporter;

public class HtmlExporter implements ReportExporter {
    @Override
    public String export(String content, String filename) {
        String outputPath = filename + ".html";
        System.out.printf("[HTML] Writing %d bytes → %s%n", content.length(), outputPath);
        return outputPath;
    }

    @Override
    public String getFileExtension() {
        return "html";
    }
}
