package edu.advising.pipeline.registration;

import edu.advising.common.PipelineHandler;
import edu.advising.common.PipelineResult;
import edu.advising.core.DatabaseManager;
import edu.advising.model.Course;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CorequisiteCheckHandler implements PipelineHandler<RegistrationContext> {
    @Override
    public PipelineResult handle(RegistrationContext ctx) {
        try {
            List<Course> corequisites = ctx.getSection().getCourse().getCorequisites();
            if (corequisites.isEmpty()) return PipelineResult.passed();

            Set<Integer> enrolledOrCompletedIds = fetchEnrolledOrCompletedCourseIds(ctx.getStudent().getId());

            List<Course> missing = corequisites.stream()
                    .filter(c -> !enrolledOrCompletedIds.contains(c.getId()))
                    .collect(Collectors.toList());

            if (!missing.isEmpty()) {
                String list = missing.stream()
                        .map(c -> c.getCode() + " (" + c.getName() + ")")
                        .collect(Collectors.joining(", "));
                return PipelineResult.failed("COREQUISITE_CHECK",
                        "Must also be enrolled in: " + list);
            }

        } catch (SQLException e) {
            return PipelineResult.failed("COREQUISITE_CHECK",
                    "Could not verify corequisites — " + e.getMessage());
        }
        return PipelineResult.passed();
    }

    private Set<Integer> fetchEnrolledOrCompletedCourseIds(int studentId) throws SQLException {
        String sql =
                "SELECT s.course_id FROM enrollments e " +
                        "JOIN sections s ON e.section_id = s.id " +
                        "WHERE e.student_id = ? AND e.status IN ('ENROLLED', 'COMPLETED')";
        return DatabaseManager.getInstance().executeQuery(sql, rs -> {
            Set<Integer> ids = new java.util.HashSet<>();
            while (rs.next()) ids.add(rs.getInt("course_id"));
            return ids;
        }, studentId);
    }
}