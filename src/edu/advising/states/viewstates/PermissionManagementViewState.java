package edu.advising.states.viewstates;

import edu.advising.contexts.ViewContext;
import edu.advising.core.DatabaseManager;
import edu.advising.model.FacultyPermission;
import edu.advising.model.Section;
import edu.advising.states.ViewState;
import edu.advising.users.Faculty;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PermissionManagementViewState implements ViewState {

    private static final PermissionManagementViewState instance = new PermissionManagementViewState();

    @Override
    public boolean requiresAuthentication() {
        return true;
    }


    public void handleAction(ViewContext ctx, String command, String... params) {
        switch (command) {
            case "APPROVE" -> ctx.getFacultyPermissionContext().approve();
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
        System.out.printf("  ---- Permission Requests for %s ----%n", faculty.getFullName());
        System.out.println();

        try {
            List<Section> sections = faculty.getSections();
            List<FacultyPermission> pending = new ArrayList<>();
            for (Section sec : sections) {
                List<FacultyPermission> perms = DatabaseManager.getInstance()
                        .fetchMany(FacultyPermission.class, "section_id", sec.getId());
                perms.stream()
                        .filter(p -> "REQUESTED".equals(p.getStatus()))
                        .forEach(pending::add);
            }

            if (pending.isEmpty()) {
                System.out.println("  No pending permission requests.");
            } else {
                System.out.printf("  %-6s  %-24s  %-14s  %s%n", "ID", "Course", "Expires", "Status");
                System.out.println("  " + "-".repeat(60));
                for (FacultyPermission fp : pending) {
                    Section sec = DatabaseManager.getInstance()
                            .fetchOne(Section.class, "id", fp.getSectionId());
                    String code    = sec != null ? sec.getCourseCode() : "Section #" + fp.getSectionId();
                    String expires = fp.getExpiryDate() != null
                            ? fp.getExpiryDate().toLocalDate().toString() : "—";
                    System.out.printf("  %-6d  %-24s  %-14s  %s%n",
                            fp.getId(), code, expires, fp.getStatus());
                }
            }
        } catch (SQLException ex) {
            System.out.println("  (Could not load requests — " + ex.getMessage() + ")");
        }

        System.out.println();
        System.out.println("  Commands: a <requestId>   [b] Back   [l] Logout   [q] Quit");
    }

    @Override
    public String getViewName() {
        return "PERMISSION_MANAGEMENT";
    }

    public static PermissionManagementViewState getInstance() {
        return instance;
    }
}
