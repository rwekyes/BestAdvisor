package edu.advising.states.viewstates;

import edu.advising.contexts.ViewContext;
import edu.advising.permissions.Restriction;
import edu.advising.permissions.RestrictionSummary;
import edu.advising.states.ViewState;
import edu.advising.users.Student;

public class RestrictionsViewState implements ViewState {

    private static final RestrictionsViewState instance = new RestrictionsViewState();

    @Override public boolean requiresAuthentication() { return true; }

    @Override
    public void handleAction(ViewContext ctx, String command, String... params) {
        switch (command) {
            case "LOGOUT" -> ctx.logout();
            default -> throw new IllegalArgumentException("Unknown command: " + command);
        }
    }

    @Override public void enter(ViewContext ctx) {}
    @Override public void exit(ViewContext ctx)  {}

    @Override
    public void render(ViewContext viewContext) {
        Student student = (Student) viewContext.getCurrentUser();
        System.out.println();
        System.out.printf("  ---- Restrictions & Holds: %s ----%n", student.getFullName());
        System.out.println();

        if (viewContext.getPermissionTree() == null) {
            System.out.println("  (Permission data not available for this session.)");
        } else {
            RestrictionSummary summary = viewContext.getPermissionTree().getRestrictions();
            if (!summary.hasRestrictions()) {
                System.out.println("  No active holds or restrictions on your account.");
            } else {
                System.out.printf("  %-28s  %-20s  %s%n", "Restriction", "Office", "Resolution");
                System.out.println("  " + "-".repeat(80));
                for (Restriction r : summary.getHolds()) {
                    String type       = r.type       != null ? r.type       : "—";
                    String office     = r.office     != null ? r.office     : "—";
                    String resolution = r.resolution != null ? r.resolution : "—";
                    System.out.printf("  %-28s  %-20s  %s%n",
                            type.length() > 28 ? type.substring(0, 28) : type,
                            office.length() > 20 ? office.substring(0, 20) : office,
                            resolution);
                }
            }
        }

        System.out.println();
        System.out.println("  Shortcuts: [b] Back   [l] Logout   [q] Quit");
    }

    @Override public String getViewName() { return "RESTRICTIONS"; }
    public static RestrictionsViewState getInstance() { return instance; }
}
