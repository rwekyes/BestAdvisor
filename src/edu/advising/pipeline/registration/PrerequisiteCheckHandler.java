package edu.advising.pipeline.registration;

import edu.advising.common.PipelineHandler;
import edu.advising.common.PipelineResult;
import edu.advising.core.DatabaseManager;
import edu.advising.model.Course;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PrerequisiteCheckHandler implements PipelineHandler<RegistrationContext> {
    @Override
    public PipelineResult handle(RegistrationContext ctx) {
        try {
            List<Course> prerequisites = ctx.getSection().getCourse().getPrerequisites();
            if (prerequisites.isEmpty()) return PipelineResult.passed();

            Set<Integer> completedCourseIds = fetchCompletedCourseIds(ctx.getStudent().getId());

            List<Course> missing = prerequisites.stream()
                    .filter(p -> !completedCourseIds.contains(p.getId()))
                    .collect(Collectors.toList());

            if (!missing.isEmpty()) {
                String list = missing.stream()
                        .map(p -> p.getCode() + " (" + p.getName() + ")")
                        .collect(Collectors.joining(", "));
                return PipelineResult.failed("PREREQUISITE_CHECK",
                        "Missing prerequisites: " + list);
            }

        } catch (SQLException e) {
            return PipelineResult.failed("PREREQUISITE_CHECK",
                    "Could not verify prerequisites — " + e.getMessage());
        }
        return PipelineResult.passed();
    }

    private Set<Integer> fetchCompletedCourseIds(int studentId) throws SQLException {
        String sql =
                "SELECT s.course_id FROM enrollments e " +
                        "JOIN sections s ON e.section_id = s.id " +
                        "WHERE e.student_id = ? AND e.status = 'COMPLETED'";
        return DatabaseManager.getInstance().executeQuery(sql, rs -> {
            Set<Integer> ids = new java.util.HashSet<>();
            while (rs.next()) ids.add(rs.getInt("course_id"));
            return ids;
        }, studentId);
    }
}