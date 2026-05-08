package edu.advising.report.excel;

import edu.advising.report.ReportRenderer;

public class ExcelRenderer implements ReportRenderer {
    @Override
    public String render(String title, String body) {
        // Produces CSV-style content that Excel can open directly
        return "sep=,\n" + title + "\n\n" + body;
    }
}
