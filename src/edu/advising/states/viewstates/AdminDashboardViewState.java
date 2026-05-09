package edu.advising.states.viewstates;

import edu.advising.contexts.ViewContext;
import edu.advising.navmenu.*;
import edu.advising.states.ViewState;
import edu.advising.users.Admin;

import java.util.List;

public class AdminDashboardViewState implements ViewState {

    private static final AdminDashboardViewState instance = new AdminDashboardViewState();

    @Override
    public boolean requiresAuthentication() {
        return true;
    }


    @Override
    public void handleAction(ViewContext ctx, String command, String... params) {
        switch(command){
            case "LOGOUT" -> ctx.logout();
            default -> throw new IllegalArgumentException("Unknown handleAction command - " + command);
        }
    }

    @Override
    public void enter(ViewContext viewContext) {

    }

    @Override
    public void exit(ViewContext viewContext) {

    }

    @Override
    public void render(ViewContext viewContext) {
        Admin admin = (Admin) viewContext.getCurrentUser();
        System.out.println();
        new AdminDashboardWidget(admin).render();
        System.out.println();
        new AdminProfilePanel(admin).render();
        System.out.println();
        new AdminNotificationPanel().render();
        System.out.println();
        new AdminQuickActionsPanel().render();
        System.out.println();
        System.out.println("--- Navigation ---");
        List<NavItem> items = new AdminNavMenuFactory().createMenuItems();
        for (int i = 0; i < items.size(); i++) {
            System.out.printf("  %2d. %s%n", i + 1, items.get(i).getLabel());
        }
        System.out.println();
        System.out.println("  Shortcuts: [l] Logout  [b] Back  [q] Quit");
    }

    @Override
    public String getViewName() {
        return "ADMIN_DASHBOARD";
    }

    public static AdminDashboardViewState getInstance() {
        return instance;
    }
}
