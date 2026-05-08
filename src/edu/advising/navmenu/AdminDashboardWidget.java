package edu.advising.navmenu;

import edu.advising.users.Admin;

public class AdminDashboardWidget implements DashboardWidget {
    private final Admin admin;

    public AdminDashboardWidget(Admin admin) {
        this.admin = admin;
    }

    @Override
    public void render() {
        System.out.println("=== Admin Dashboard ===");
        System.out.println("Welcome, " + admin.getFullName());
    }

    @Override
    public void refresh() {
    }
}
