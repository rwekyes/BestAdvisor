package edu.advising.navmenu;

import java.util.ArrayList;
import java.util.List;

public class AdminReportViewer implements ReportViewer {
    @Override
    public List<NavItem> getAvailableReports() {
        List<NavItem> reports = new ArrayList<>();
        reports.addAll(new StudentReportViewer().getAvailableReports());
        reports.addAll(new FacultyReportViewer().getAvailableReports());
        reports.add(new NavItem("Audit Log Export", "REPORT_AUDIT_LOG", NavIcons.ICON_NAV));
        return reports;
    }

    @Override
    public void render() {
        System.out.println("=== Admin Reports ===");
        getAvailableReports().forEach(r -> System.out.println("  • " + r.getLabel()));
    }
}
