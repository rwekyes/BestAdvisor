package edu.advising.navmenu;

import edu.advising.users.Faculty;

import java.util.Collections;
import java.util.List;

public class FacultyNavMenuFactory implements NavMenuFactory{
    private final Faculty faculty;

    public static List<NavItem> facultyItems() {
        return List.of(
                new NavItem("My Schedule", "SCHEDULE", NavIcons.ICON_NAV),
                new NavItem("Class Roster", "ROSTER", NavIcons.ICON_NAV),
                new NavItem("Submit Grades", "SUBMIT_GRADES", NavIcons.ICON_NAV),
                new NavItem("Section Search", "SECTION_SEARCH", NavIcons.ICON_NAV),
                new NavItem("Student Profiles", "STUDENT_PROFILES", NavIcons.ICON_NAV),
                new NavItem("Permission To Add", "PERMISSION", NavIcons.ICON_NAV),
                new NavItem("Budget Summary", "BUDGET_SUMMARY", NavIcons.ICON_NAV),
                new NavItem("Documents", "DOCUMENTS", NavIcons.ICON_NAV)
        );
    }

    public FacultyNavMenuFactory(Faculty faculty) { this.faculty = faculty; }
    @Override
    public List<NavItem> createMenuItems() {
        return Collections.unmodifiableList(facultyItems());
    }

    @Override
    public DashboardWidget createDashboardWidget() {
        return new FacultyDashboardWidget(faculty);
    }

    @Override
    public NotificationPanel createNotificationPanel() {
        return new FacultyNotificationPanel();
    }

    @Override
    public QuickActionPanel createQuickActionsPanel() {
        return new FacultyQuickActionsPanel();
    }

    @Override
    public ProfilePanel createProfilePanel() {
        return null;
    }

    @Override
    public ReportViewer createReportViewer() {
        return null;
    }
}
