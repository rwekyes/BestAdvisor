package edu.advising.states.viewstates;

import edu.advising.commands.RegisterCommand;
import edu.advising.model.Enrollment;
import edu.advising.model.RegistrationPeriod;
import edu.advising.model.Section;
import edu.advising.contexts.ViewContext;
import edu.advising.core.DatabaseManager;
import edu.advising.notifications.ObservableStudent;
import edu.advising.states.ViewState;
import edu.advising.users.Student;

import java.sql.SQLException;
import java.util.List;

public class RegistrationViewState implements ViewState {

    private static final RegistrationViewState instance = new RegistrationViewState();

    @Override
    public boolean requiresAuthentication() {
        return true;
    }

    public void handleAction(ViewContext ctx, String command, String... params) {
        switch (command) {
            case "CHECK_STATUS" -> render(ctx, params[0], params[1]);
            case "REGISTER" -> {
                try {
                    int sectionId = Integer.parseInt(params[0]);
                    Section section = DatabaseManager.getInstance()
                            .fetchOne(Section.class, "id", sectionId);
                    if (section == null) {
                        System.out.println("Section not found.");
                        return;
                    }
                    ObservableStudent student = ObservableStudent.fromSuperType(
                            (Student) ctx.getCurrentUser()
                    );
                    RegisterCommand cmd = new RegisterCommand(
                            student, section, ctx.getPermissionTree()
                    );
                    ctx.getCommandExecutor().execute(cmd);
                } catch (SQLException e) {
                    System.out.println("Registration failed - database error.");
                }
            }
            case "LOGOUT" -> ctx.logout();
            default -> throw new IllegalArgumentException(
                    "Unknown handleAction command - " + command
            );
        };
    }

    @Override
    public void enter(ViewContext viewContext) {

    }

    @Override
    public void exit(ViewContext viewContext) {

    }

    @Override
    public void render(ViewContext viewContext) {
        Student student = (Student) viewContext.getCurrentUser();
        System.out.println();
        System.out.println("  ---- Register For Classes ----");
        System.out.println();

        // Current enrollments
        try {
            List<Enrollment> current = DatabaseManager.getInstance()
                    .fetchMany(Enrollment.class, "student_id", student.getId());
            List<Enrollment> active = current.stream()
                    .filter(e -> "ENROLLED".equals(e.getStatus()) || "PENDING".equals(e.getStatus()))
                    .toList();
            if (!active.isEmpty()) {
                System.out.println("  Currently enrolled:");
                for (Enrollment e : active) {
                    Section sec = e.getSection();
                    if (sec == null) continue;
                    System.out.printf("    [%4d] %-24s  %-28s  %s%n",
                            sec.getId(), sec.getCourseCode(), sec.getCourseName(), e.getStatus());
                }
                System.out.println();
            }
        } catch (SQLException ex) {
            System.out.println("  (Could not load current enrollments — " + ex.getMessage() + ")");
        }

        // Open sections available to register
        try {
            List<Section> open = DatabaseManager.getInstance()
                    .fetchMany(Section.class, "status", "OPEN");
            if (open.isEmpty()) {
                System.out.println("  No open sections available at this time.");
            } else {
                System.out.println("  Open sections — type 'reg <ID>' or just the ID to register:");
                System.out.printf("  %-6s  %-24s  %-28s  %-10s  %s%n",
                        "ID", "Course", "Title", "Seats", "Room");
                System.out.println("  " + "-".repeat(82));
                for (Section s : open) {
                    String seats = s.getEnrolled() + "/" + s.getCapacity();
                    String room  = s.getRoom() != null ? s.getRoom() : "TBA";
                    System.out.printf("  %-6d  %-24s  %-28s  %-10s  %s%n",
                            s.getId(),
                            s.getCourseCode().length() > 24 ? s.getCourseCode().substring(0, 24) : s.getCourseCode(),
                            s.getCourseName().length() > 28 ? s.getCourseName().substring(0, 28) : s.getCourseName(),
                            seats, room);
                }
            }
        } catch (SQLException ex) {
            System.out.println("  (Could not load open sections — " + ex.getMessage() + ")");
        }

        System.out.println();
        System.out.println("  Commands: reg <sectionId>   [u] Undo   [b] Back   [l] Logout   [q] Quit");
    }

    public void render(ViewContext viewContext, String p1, String p2) {
        RegistrationPeriod currentPeriod = viewContext.getRegistrationPeriodContext().getRegistrationPeriod();
        //TODO: Use the view template object to make the Strings needed
    }

    @Override
    public String getViewName() {
        return "REGISTRATION";
    }

    public static RegistrationViewState getInstance() {
        return instance;
    }
}
