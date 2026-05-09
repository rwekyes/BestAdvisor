package edu.advising.navmenu;

import java.util.ArrayList;
import java.util.List;

public class StudentReportViewer implements ReportViewer {
    @Override
    public List<NavItem> getAvailableReports() {
        List<NavItem> reports = new ArrayList<>();
        reports.add(new NavItem("Transcript",        "REPORT_TRANSCRIPT",     NavIcons.ICON_NAV));
        reports.add(new NavItem("Program Evaluation","REPORT_PROGRAM_EVAL",   NavIcons.ICON_NAV));
        reports.add(new NavItem("Educational Plan",  "REPORT_EDUCATIONAL_PLAN",NavIcons.ICON_NAV));
        reports.add(new NavItem("Financial Summary", "REPORT_FINANCIAL",      NavIcons.ICON_NAV));
        return reports;
    }

    @Override
    public void render() {
        System.out.println("=== Student Reports ===");
        getAvailableReports().forEach(r -> System.out.println("  • " + r.getLabel()));
    }
}
