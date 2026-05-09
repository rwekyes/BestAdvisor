package edu.advising.report;

import edu.advising.report.excel.ExcelReportOutputFactory;
import edu.advising.report.html.HtmlReportOutputFactory;
import edu.advising.report.pdf.PdfReportOutputFactory;

public class ReportOutputFactoryProvider {
    public enum OutputFormat { PDF, HTML, EXCEL }

    public static ReportOutputFactory getFactory(OutputFormat format) {
        return switch (format) {
            case PDF   -> new PdfReportOutputFactory();
            case HTML  -> new HtmlReportOutputFactory();
            case EXCEL -> new ExcelReportOutputFactory();
        };
    }
}
