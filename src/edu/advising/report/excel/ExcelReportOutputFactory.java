package edu.advising.report.excel;

import edu.advising.report.ReportExporter;
import edu.advising.report.ReportFormatter;
import edu.advising.report.ReportOutputFactory;
import edu.advising.report.ReportRenderer;

public class ExcelReportOutputFactory implements ReportOutputFactory {
    @Override
    public ReportRenderer createRenderer()   { return new ExcelRenderer(); }
    @Override
    public ReportFormatter createFormatter() { return new ExcelFormatter(); }
    @Override
    public ReportExporter createExporter()   { return new ExcelExporter(); }
}
