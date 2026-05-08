package edu.advising.report.html;

import edu.advising.report.ReportExporter;
import edu.advising.report.ReportFormatter;
import edu.advising.report.ReportOutputFactory;
import edu.advising.report.ReportRenderer;

public class HtmlReportOutputFactory implements ReportOutputFactory {
    @Override
    public ReportRenderer createRenderer()   { return new HtmlRenderer(); }
    @Override
    public ReportFormatter createFormatter() { return new HtmlFormatter(); }
    @Override
    public ReportExporter createExporter()   { return new HtmlExporter(); }
}
