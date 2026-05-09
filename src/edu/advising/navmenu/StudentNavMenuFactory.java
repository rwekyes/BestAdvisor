package edu.advising.navmenu;

import edu.advising.users.Student;

import java.util.Collections;
import java.util.List;

public class StudentNavMenuFactory implements NavMenuFactory{
    private final Student student;

    public static List<NavItem> studentItems() {
        return List.of(
            new NavItem("My Schedule", "SCHEDULE", NavIcons.ICON_NAV),
            new NavItem("Register For Classes", "REGISTER", NavIcons.ICON_NAV),
            new NavItem("Grades", "GRADES", NavIcons.ICON_NAV),
            new NavItem("Financial Information", "FINANCIAL_INFO", NavIcons.ICON_NAV),
            new NavItem("Financial Aid", "FINANCIAL_AID", NavIcons.ICON_NAV),
            new NavItem("Transcript", "TRANSCRIPT", NavIcons.ICON_NAV),
            new NavItem("Program Evaluation", "PROGRAM_EVAL", NavIcons.ICON_NAV),
            new NavItem("My Educational Plan", "EDUCATIONAL_PLAN", NavIcons.ICON_NAV),
            new NavItem("Documents", "DOCUMENTS", NavIcons.ICON_NAV),
            new NavItem("Restrictions", "RESTRICTIONS", NavIcons.ICON_NAV)
        );
    }

    public StudentNavMenuFactory(Student student) {
        this.student = student;
    }
    @Override
    public List<NavItem> createMenuItems() {
        return Collections.unmodifiableList(studentItems());
    }

    @Override
    public DashboardWidget createDashboardWidget(){
        return new StudentDashboardWidget(this.student);
    }

    @Override
    public NotificationPanel createNotificationPanel() {
        return new StudentNotificationPanel();
    }

    @Override
    public QuickActionPanel createQuickActionsPanel() {
        return new StudentQuickActionsPanel();
    }

    @Override
    public ProfilePanel createProfilePanel() {
        return new StudentProfilePanel(this.student);
    }

    @Override
    public ReportViewer createReportViewer() {
        return new StudentReportViewer();
    }
}
