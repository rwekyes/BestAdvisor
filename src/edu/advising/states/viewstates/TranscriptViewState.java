package edu.advising.states.viewstates;

import edu.advising.contexts.ViewContext;
import edu.advising.core.DatabaseManager;
import edu.advising.model.Enrollment;
import edu.advising.model.Section;
import edu.advising.states.ViewState;
import edu.advising.users.Student;

import java.sql.SQLException;
import java.util.List;

public class TranscriptViewState implements ViewState {

    private static final TranscriptViewState instance = new TranscriptViewState();

    @Override
    public boolean requiresAuthentication() {
        return true;
    }

    @Override
    public void handleAction(ViewContext ctx, String command, String... params){
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
        Student student = (Student) viewContext.getCurrentUser();
        System.out.println();
        System.out.printf("  ---- Transcript: %s (%s) ----%n",
                student.getFullName(), student.getStudentId());
        System.out.printf("  Major: %-22s  Standing: %-14s  GPA: %s%n",
                student.getMajor()            != null ? student.getMajor()            : "Undeclared",
                student.getAcademicStanding() != null ? student.getAcademicStanding() : "—",
                student.getGpa()              != null ? student.getGpa().toPlainString() : "N/A");
        System.out.println();

        try {
            List<Enrollment> enrollments = DatabaseManager.getInstance()
                    .fetchMany(Enrollment.class, "student_id", student.getId());

            if (enrollments.isEmpty()) {
                System.out.println("  No enrollment records found.");
            } else {
                System.out.printf("  %-22s  %-30s  %7s  %6s  %-10s%n",
                        "Course", "Title", "Credits", "Grade", "Status");
                System.out.println("  " + "-".repeat(82));
                for (Enrollment e : enrollments) {
                    Section sec = e.getSection();
                    if (sec == null) continue;
                    String code   = sec.getCourseCode();
                    String name   = sec.getCourseName();
                    String grade  = e.getFinalGrade() != null ? e.getFinalGrade()
                                  : e.getGrade()      != null ? e.getGrade() : "—";
                    double credits = 0;
                    try { credits = sec.getCourse() != null ? sec.getCourse().getCredits() : 0; }
                    catch (SQLException ignored) {}
                    System.out.printf("  %-22s  %-30s  %7.1f  %6s  %-10s%n",
                            code.length() > 22 ? code.substring(0, 22) : code,
                            name.length() > 30 ? name.substring(0, 30) : name,
                            credits, grade, e.getStatus());
                }
                System.out.println("  " + "-".repeat(82));
                System.out.printf("  Total Credits Earned: %d    Cumulative GPA: %s%n",
                        student.getCreditsEarned(),
                        student.getGpa() != null ? student.getGpa().toPlainString() : "N/A");
            }
        } catch (SQLException ex) {
            System.out.println("  (Unable to load transcript — " + ex.getMessage() + ")");
        }

        System.out.println();
        System.out.println("  Shortcuts: [b] Back   [l] Logout   [q] Quit");
    }

    @Override
    public String getViewName() {
        return "TRANSCRIPT_VIEW";
    }

    public static TranscriptViewState getInstance() {
        return instance;
    }

}
