package edu.advising.report.pdf;

import edu.advising.report.ReportExporter;
import edu.advising.report.ReportFormatter;
import edu.advising.report.ReportOutputFactory;
import edu.advising.report.ReportRenderer;

public class PdfReportOutputFactory implements ReportOutputFactory {
    @Override
    public ReportRenderer createRenderer()   { return new PdfRenderer(); }
    @Override
    public ReportFormatter createFormatter() { return new PdfFormatter(); }
    @Override
    public ReportExporter createExporter()   { return new PdfExporter(); }
}
