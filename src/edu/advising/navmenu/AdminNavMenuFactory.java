package edu.advising.navmenu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdminNavMenuFactory implements NavMenuFactory{
    @Override
    public List<NavItem> createMenuItems() {
        List<NavItem> items = new ArrayList<>();
        items.addAll(StudentNavMenuFactory.studentItems());
        items.addAll(FacultyNavMenuFactory.facultyItems());
        items.addAll(List.of(
                new NavItem("Manage Users",               "USER_MGMT",            NavIcons.ICON_NAV),
                new NavItem("Audit Log",                  "AUDIT_LOG",            NavIcons.ICON_NAV),
                new NavItem("Advance Registration State", "ADVANCE_REGISTRATION", NavIcons.ICON_NAV),
                new NavItem("Manage Restrictions",        "RESTRICTION_MGMT",     NavIcons.ICON_NAV),
                new NavItem("System Configuration",       "CONFIG",               NavIcons.ICON_NAV)
        ));
        return Collections.unmodifiableList(items);
    }

    @Override
    public DashboardWidget createDashboardWidget() {
        return null;
    }

    @Override
    public NotificationPanel createNotificationPanel() {
        return null;
    }

    @Override
    public QuickActionPanel createQuickActionsPanel() {
        return null;
    }

    @Override
    public ProfilePanel createProfilePanel() {
        return null;
    }

    @Override
    public ReportViewer createReportViewer() {
        return new AdminReportViewer();
    }

    @Override
    public NavItem createMenu() {
        return null;
    }
}
