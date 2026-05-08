package edu.advising.report;

public interface ReportExporter {
    String export(String content, String filename);
    String getFileExtension();
}
