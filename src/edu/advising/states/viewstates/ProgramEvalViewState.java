package edu.advising.states.viewstates;

import edu.advising.contexts.ViewContext;
import edu.advising.core.DatabaseManager;
import edu.advising.model.Enrollment;
import edu.advising.model.Section;
import edu.advising.states.ViewState;
import edu.advising.users.Student;

import java.sql.SQLException;
import java.util.List;

public class ProgramEvalViewState implements ViewState {

    private static final ProgramEvalViewState instance = new ProgramEvalViewState();

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
        System.out.printf("  ---- Program Evaluation: %s ----%n", student.getFullName());
        System.out.println();

        System.out.printf("  Major:     %-30s  Classification: %s%n",
                student.getMajor()            != null ? student.getMajor()            : "Undeclared",
                student.getClassification()   != null ? student.getClassification()   : "—");
        System.out.printf("  Minor:     %-30s  Standing:       %s%n",
                student.getMinor()            != null ? student.getMinor()            : "None",
                student.getAcademicStanding() != null ? student.getAcademicStanding() : "—");
        System.out.printf("  GPA:       %-10s  Credits Earned: %d%n",
                student.getGpa() != null ? student.getGpa().toPlainString() : "N/A",
                student.getCreditsEarned());
        System.out.println();

        try {
            List<Enrollment> enrollments = DatabaseManager.getInstance()
                    .fetchMany(Enrollment.class, "student_id", student.getId());

            long completed = enrollments.stream()
                    .filter(e -> "COMPLETED".equals(e.getStatus())).count();
            long active = enrollments.stream()
                    .filter(e -> "ENROLLED".equals(e.getStatus()) || "PENDING".equals(e.getStatus())).count();
            long dropped = enrollments.stream()
                    .filter(e -> "DROPPED".equals(e.getStatus()) || "WITHDRAWN".equals(e.getStatus())).count();

            System.out.println("  Enrollment Summary:");
            System.out.printf("    Completed: %d   Active: %d   Dropped/Withdrawn: %d%n",
                    completed, active, dropped);
            System.out.println();

            if (!enrollments.isEmpty()) {
                System.out.println("  Completed Courses:");
                System.out.printf("  %-22s  %-30s  %6s  %s%n", "Course", "Title", "Grade", "Credits");
                System.out.println("  " + "-".repeat(72));
                double totalCredits = 0;
                for (Enrollment e : enrollments) {
                    if (!"COMPLETED".equals(e.getStatus())) continue;
                    Section sec = e.getSection();
                    if (sec == null) continue;
                    String grade = e.getFinalGrade() != null ? e.getFinalGrade()
                                 : e.getGrade()      != null ? e.getGrade() : "—";
                    double credits = 0;
                    try { credits = sec.getCourse() != null ? sec.getCourse().getCredits() : 0; }
                    catch (SQLException ignored) {}
                    totalCredits += credits;
                    System.out.printf("  %-22s  %-30s  %6s  %.1f%n",
                            sec.getCourseCode(), sec.getCourseName(), grade, credits);
                }
                System.out.println("  " + "-".repeat(72));
                System.out.printf("  Total Completed Credits: %.1f%n", totalCredits);
            }
        } catch (SQLException ex) {
            System.out.println("  (Could not load program data — " + ex.getMessage() + ")");
        }

        System.out.println();
        System.out.println("  Shortcuts: [b] Back   [l] Logout   [q] Quit");
    }

    @Override public String getViewName() { return "PROGRAM_EVAL"; }
    public static ProgramEvalViewState getInstance() { return instance; }
}
