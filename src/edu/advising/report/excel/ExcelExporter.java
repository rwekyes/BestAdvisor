package edu.advising.report.excel;

import edu.advising.report.ReportExporter;

public class ExcelExporter implements ReportExporter {
    @Override
    public String export(String content, String filename) {
        String outputPath = filename + ".xlsx";
        System.out.printf("[Excel] Writing %d bytes → %s%n", content.length(), outputPath);
        return outputPath;
    }

    @Override
    public String getFileExtension() {
        return "xlsx";
    }
}
