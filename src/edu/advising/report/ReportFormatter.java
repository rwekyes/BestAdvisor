package edu.advising.report;

import java.util.List;

public interface ReportFormatter {
    String formatTable(List<String> headers, List<List<String>> rows);
    String formatHeader(String institution, String reportName, String generatedBy);
    String formatFooter(String notes);
}
