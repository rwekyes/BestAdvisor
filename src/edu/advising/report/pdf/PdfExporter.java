package edu.advising.report.pdf;

import edu.advising.report.ReportExporter;

public class PdfExporter implements ReportExporter {
    @Override
    public String export(String content, String filename) {
        String outputPath = filename + ".pdf";
        System.out.printf("[PDF] Writing %d bytes → %s%n", content.length(), outputPath);
        return outputPath;
    }

    @Override
    public String getFileExtension() {
        return "pdf";
    }
}
