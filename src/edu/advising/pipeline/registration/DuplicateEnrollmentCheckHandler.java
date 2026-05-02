package edu.advising.pipeline.registration;

import edu.advising.common.PipelineHandler;
import edu.advising.common.PipelineResult;
import edu.advising.core.DatabaseManager;

import java.sql.SQLException;
import java.util.Set;

public class DuplicateEnrollmentCheckHandler implements PipelineHandler<RegistrationContext> {
    @Override
    public PipelineResult handle(RegistrationContext ctx) {
        try {
            Set<Integer> enrolledCourses = fetchEnrolledCourseIds(ctx.getStudent().getId());
                if(enrolledCourses.contains(ctx.getSection().getCourseId())){
                    return PipelineResult.failed("DUPLICATE_ENROLLMENT_CHECK",
                            ctx.getStudent().getFullName() + " is already enrolled in " +
                                    ctx.getSection().getCourseName());
                }

        } catch (SQLException e) {
            return PipelineResult.failed("DUPLICATE_ENROLLMENT_CHECK",
                    "Could not verify current enrollments - " + e.getMessage());
        }
        return PipelineResult.passed();
    }

    private Set<Integer> fetchEnrolledCourseIds(int studentId) throws SQLException {
        String sql =
                "SELECT s.course_id FROM enrollments e " +
                        "JOIN sections s ON e.section_id = s.id " +
                        "WHERE e.student_id = ? AND e.status = 'ENROLLED'";
        return DatabaseManager.getInstance().executeQuery(sql, rs -> {
            Set<Integer> ids = new java.util.HashSet<>();
            while (rs.next()) ids.add(rs.getInt("course_id"));
            return ids;
        }, studentId);
    }
}
