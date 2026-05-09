package edu.advising.report.excel;

import edu.advising.report.ReportFormatter;

import java.util.List;
import java.util.stream.Collectors;

public class ExcelFormatter implements ReportFormatter {

    @Override
    public String formatTable(List<String> headers, List<List<String>> rows) {
        StringBuilder sb = new StringBuilder();
        sb.append(headers.stream().map(this::escape).collect(Collectors.joining(","))).append("\n");
        for (List<String> row : rows) {
            sb.append(row.stream().map(this::escape).collect(Collectors.joining(","))).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String formatHeader(String institution, String reportName, String generatedBy) {
        return escape(institution) + "," + escape(reportName) + "," + escape(generatedBy) + "\n\n";
    }

    @Override
    public String formatFooter(String notes) {
        return "\n" + escape(notes) + "\n";
    }

    private String escape(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
