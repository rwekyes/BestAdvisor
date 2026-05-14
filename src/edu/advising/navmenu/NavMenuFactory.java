package edu.advising.navmenu;

import java.util.List;

public interface NavMenuFactory {
    List<NavItem> createMenuItems();
    DashboardWidget createDashboardWidget();
    NotificationPanel createNotificationPanel();
    QuickActionPanel createQuickActionsPanel();
    ProfilePanel createProfilePanel();
    ReportViewer createReportViewer();

    NavItem createMenu();
}
