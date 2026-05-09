package edu.advising.states.viewstates;

import edu.advising.contexts.ViewContext;
import edu.advising.core.DatabaseManager;
import edu.advising.model.Enrollment;
import edu.advising.model.Section;
import edu.advising.plan.EducationalPlanNode;
import edu.advising.plan.PlannedCourseNode;
import edu.advising.plan.PlannedSemesterNode;
import edu.advising.plan.PlanNode;
import edu.advising.states.ViewState;
import edu.advising.users.Student;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EducationalPlanViewState implements ViewState {

    private static final EducationalPlanViewState instance = new EducationalPlanViewState();

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
        System.out.printf("  ---- My Educational Plan: %s ----%n", student.getFullName());
        System.out.println();

        try {
            List<Enrollment> enrollments = DatabaseManager.getInstance()
                    .fetchMany(Enrollment.class, "student_id", student.getId());

            if (enrollments.isEmpty()) {
                System.out.println("  No enrollment history to build a plan from.");
            } else {
                // Group enrollments by "SEMESTER YEAR" — build the composite tree
                Map<String, List<PlanNode>> semesterMap = new LinkedHashMap<>();
                for (Enrollment e : enrollments) {
                    Section sec = e.getSection();
                    if (sec == null) continue;
                    String key = sec.getSemester() + " " + sec.getYear();
                    semesterMap.computeIfAbsent(key, k -> new ArrayList<>());
                    try {
                        if (sec.getCourse() != null) {
                            semesterMap.get(key).add(new PlannedCourseNode(sec.getCourse()));
                        }
                    } catch (SQLException ignored) {}
                }

                List<PlanNode> semesters = new ArrayList<>();
                for (Map.Entry<String, List<PlanNode>> entry : semesterMap.entrySet()) {
                    semesters.add(new PlannedSemesterNode(entry.getKey(), entry.getValue()));
                }

                EducationalPlanNode plan = new EducationalPlanNode(
                        student.getFullName() + "'s Educational Plan", semesters);

                System.out.printf("  %s  (%.1f total credits)%n",
                        plan.getDisplayName(), plan.getTotalCredits());
                System.out.println();

                for (PlanNode sem : plan.getChildren()) {
                    System.out.printf("  [%s]  %.1f credits%n",
                            sem.getDisplayName(), sem.getTotalCredits());
                    for (PlanNode course : sem.getChildren()) {
                        System.out.printf("      %-30s  %.1f cr%n",
                                course.getDisplayName(), course.getTotalCredits());
                    }
                    System.out.println();
                }
            }
        } catch (SQLException ex) {
            System.out.println("  (Could not build educational plan — " + ex.getMessage() + ")");
        }

        System.out.println("  Shortcuts: [b] Back   [l] Logout   [q] Quit");
    }

    @Override public String getViewName() { return "EDUCATIONAL_PLAN"; }
    public static EducationalPlanViewState getInstance() { return instance; }
}
