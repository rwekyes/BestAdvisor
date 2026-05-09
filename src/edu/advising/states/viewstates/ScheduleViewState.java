package edu.advising.states.viewstates;

import edu.advising.contexts.ViewContext;
import edu.advising.core.DatabaseManager;
import edu.advising.model.Enrollment;
import edu.advising.model.Section;
import edu.advising.model.SectionMeetingTime;
import edu.advising.states.ViewState;
import edu.advising.users.Student;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class ScheduleViewState implements ViewState {

    private static final ScheduleViewState instance = new ScheduleViewState();

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
        System.out.printf("  ---- My Schedule: %s ----%n", student.getFullName());
        System.out.println();

        try {
            List<Enrollment> enrollments = DatabaseManager.getInstance()
                    .fetchMany(Enrollment.class, "student_id", student.getId());
            List<Enrollment> active = enrollments.stream()
                    .filter(e -> "ENROLLED".equals(e.getStatus()) || "PENDING".equals(e.getStatus()))
                    .toList();

            if (active.isEmpty()) {
                System.out.println("  You are not currently enrolled in any sections.");
            } else {
                for (Enrollment e : active) {
                    Section sec = e.getSection();
                    if (sec == null) continue;

                    System.out.printf("  %-24s  %s%n", sec.getCourseCode(), sec.getCourseName());

                    List<SectionMeetingTime> times = sec.getMeetingTimes();
                    if (times.isEmpty()) {
                        System.out.println("    Days/Times: TBA");
                    } else {
                        String days = times.stream()
                                .map(t -> t.getDayOfWeek().name().substring(0, 3))
                                .distinct()
                                .collect(Collectors.joining("/"));
                        String timeRange = times.get(0).getStartTime() + " – " + times.get(0).getEndTime();
                        String room = times.get(0).getRoom() != null ? times.get(0).getRoom()
                                    : (sec.getRoom() != null ? sec.getRoom() : "TBA");
                        System.out.printf("    %s  %s  Room: %s  [%s]%n",
                                days, timeRange, room, e.getStatus());
                    }
                    System.out.println();
                }
            }
        } catch (SQLException ex) {
            System.out.println("  (Could not load schedule — " + ex.getMessage() + ")");
        }

        System.out.println("  Shortcuts: [b] Back   [l] Logout   [q] Quit");
    }

    @Override public String getViewName() { return "MY_SCHEDULE"; }
    public static ScheduleViewState getInstance() { return instance; }
}
