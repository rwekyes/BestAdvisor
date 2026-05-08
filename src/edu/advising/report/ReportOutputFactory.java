package edu.advising.report;

public interface ReportOutputFactory {
    ReportRenderer  createRenderer();
    ReportFormatter createFormatter();
    ReportExporter  createExporter();
}
