package edu.advising.navmenu;

import java.util.ArrayList;
import java.util.List;

public class FacultyReportViewer implements ReportViewer {
    @Override
    public List<NavItem> getAvailableReports() {
        List<NavItem> reports = new ArrayList<>();
        reports.add(new NavItem("Class Roster",    "REPORT_ROSTER",        NavIcons.ICON_NAV));
        reports.add(new NavItem("Grade Summary",   "REPORT_GRADE_SUMMARY", NavIcons.ICON_NAV));
        reports.add(new NavItem("Budget Summary",  "REPORT_BUDGET",        NavIcons.ICON_NAV));
        reports.add(new NavItem("Section Enrollment","REPORT_ENROLLMENT",  NavIcons.ICON_NAV));
        return reports;
    }

    @Override
    public void render() {
        System.out.println("=== Faculty Reports ===");
        getAvailableReports().forEach(r -> System.out.println("  • " + r.getLabel()));
    }
}
