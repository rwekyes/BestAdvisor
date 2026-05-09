package edu.advising.states.viewstates;

import edu.advising.contexts.ViewContext;
import edu.advising.core.DatabaseManager;
import edu.advising.model.Enrollment;
import edu.advising.model.Section;
import edu.advising.states.ViewState;
import edu.advising.users.Student;

import java.sql.SQLException;
import java.util.List;

public class GradesViewState implements ViewState {

    private static final GradesViewState instance = new GradesViewState();

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
        System.out.printf("  ---- Grades: %s ----%n", student.getFullName());
        System.out.println();

        try {
            List<Enrollment> enrollments = DatabaseManager.getInstance()
                    .fetchMany(Enrollment.class, "student_id", student.getId());

            if (enrollments.isEmpty()) {
                System.out.println("  No grade records found.");
            } else {
                System.out.printf("  %-22s  %-28s  %8s  %7s  %6s  %-10s%n",
                        "Course", "Title", "Midterm", "Final", "Pts", "Status");
                System.out.println("  " + "-".repeat(90));
                for (Enrollment e : enrollments) {
                    Section sec = e.getSection();
                    if (sec == null) continue;
                    String code     = sec.getCourseCode();
                    String name     = sec.getCourseName();
                    String midterm  = e.getMidtermGrade() != null ? e.getMidtermGrade() : "—";
                    String finalGr  = e.getFinalGrade()   != null ? e.getFinalGrade()
                                    : e.getGrade()         != null ? e.getGrade() : "—";
                    String pts      = e.getGradePoints()  != null ? e.getGradePoints().toPlainString() : "—";
                    System.out.printf("  %-22s  %-28s  %8s  %7s  %6s  %-10s%n",
                            code.length() > 22 ? code.substring(0, 22) : code,
                            name.length() > 28 ? name.substring(0, 28) : name,
                            midterm, finalGr, pts, e.getStatus());
                }
                System.out.println("  " + "-".repeat(90));
                System.out.printf("  Cumulative GPA: %s%n",
                        student.getGpa() != null ? student.getGpa().toPlainString() : "N/A");
            }
        } catch (SQLException ex) {
            System.out.println("  (Could not load grades — " + ex.getMessage() + ")");
        }

        System.out.println();
        System.out.println("  Shortcuts: [b] Back   [l] Logout   [q] Quit");
    }

    @Override public String getViewName() { return "GRADES"; }
    public static GradesViewState getInstance() { return instance; }
}
