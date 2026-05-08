package edu.advising.report.pdf;

import edu.advising.report.ReportRenderer;

public class PdfRenderer implements ReportRenderer {
    @Override
    public String render(String title, String body) {
        return "%PDF-1.4\n" +
               "Title: " + title + "\n" +
               body + "\n" +
               "%%EOF";
    }
}
