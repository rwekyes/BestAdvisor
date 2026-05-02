package edu.advising.pipeline.registration;

import edu.advising.common.PipelineHandler;
import edu.advising.common.PipelineResult;
import edu.advising.core.DatabaseManager;

import java.sql.SQLException;

public class CreditLimitCheckHandler implements PipelineHandler<RegistrationContext> {
    @Override
    public PipelineResult handle(RegistrationContext context) {
        try{
            double totalCredits = fetchEnrolledCreditsForTerm(
                    context.getStudent().getId(),
                    context.getSection().getSemester(),
                    context.getSection().getYear());
            if(totalCredits + context.getSection().getCourse().getCredits() > 25){
                return PipelineResult.failed("CREDIT_LIMIT_CHECK",
                        "Enrollment will exceed maximum credit limit of 25 enrolled credits. " +
                                context.getStudent().getFullName() +
                                " is currently enrolled in " +
                                totalCredits +
                                " credits.");
            }
        } catch (SQLException e) {
            return PipelineResult.failed("CREDIT_LIMIT_CHECK",
                    "Could not verify currently enrolled credits — " + e.getMessage());
        }
        return PipelineResult.passed();
    }

    // Raw SQL for filtering out just this semester's enrolled credits
    private double fetchEnrolledCreditsForTerm(int studentId, String semester, int year) throws SQLException {
        String sql =
                "SELECT COALESCE(SUM(c.credits), 0) FROM enrollments e " +
                        "JOIN sections s ON e.section_id = s.id " +
                        "JOIN courses c ON s.course_id = c.id " +
                        "WHERE e.student_id = ? AND e.status = 'ENROLLED' " +
                        "AND s.semester = ? AND s.year = ?";
        return DatabaseManager.getInstance().executeQuery(sql,
                rs -> rs.next() ? rs.getDouble(1) : 0.0,
                studentId, semester, year);
    }
}
