package edu.advising.states.viewstates;

import edu.advising.contexts.ViewContext;
import edu.advising.navmenu.*;
import edu.advising.states.ViewState;
import edu.advising.users.Faculty;

import java.util.List;

public class FacultyDashboardViewState implements ViewState {

    private static final FacultyDashboardViewState instance = new FacultyDashboardViewState();

    @Override
    public boolean requiresAuthentication() {
        return true;
    }

    @Override
    public void handleAction(ViewContext ctx, String command, String... params) {
        switch(command){
            case "NAVIGATE" -> {
                switch (params[0]) {
                    case "PERMISSIONS" -> ctx.navigateTo(PermissionManagementViewState.getInstance());
                    default -> throw new IllegalArgumentException("Unknown handleAction param1 - " + params[0]);
                }
            }
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
        Faculty faculty = (Faculty) viewContext.getCurrentUser();
        System.out.println();
        new FacultyDashboardWidget(faculty).render();
        System.out.println();
        new FacultyProfilePanel(faculty).render();
        System.out.println();
        new FacultyNotificationPanel().render();
        System.out.println();
        new FacultyQuickActionsPanel().render();
        System.out.println();
        System.out.println("--- Navigation ---");
        List<NavItem> items = FacultyNavMenuFactory.facultyItems();
        for (int i = 0; i < items.size(); i++) {
            System.out.printf("  %2d. %s%n", i + 1, items.get(i).getLabel());
        }
        System.out.println();
        System.out.println("  Shortcuts: [p] Permission To Add  [l] Logout  [b] Back  [q] Quit");
    }

    @Override
    public String getViewName() {
        return "FACULTY_DASHBOARD";
    }

    public static FacultyDashboardViewState getInstance() {
        return instance;
    }
}
