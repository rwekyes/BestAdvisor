package edu.advising.pipeline.registration;

import edu.advising.common.PipelineHandler;
import edu.advising.common.PipelineResult;
import edu.advising.core.DatabaseManager;

import java.sql.SQLException;

public class AcademicHoldCheckHandler implements PipelineHandler<RegistrationContext> {
    @Override
    public PipelineResult handle(RegistrationContext context) {
        try {
            String hold = fetchActiveHold(context.getStudent().getId(), "ACADEMIC_HOLD");
            if (hold != null) {
                return PipelineResult.failed("ACADEMIC_HOLD_CHECK",
                        hold + " — Contact the Academic Office to resolve this hold.");
            }
        }catch (SQLException e){
            return PipelineResult.failed("ACADEMIC_HOLD_CHECK",
                    "Error retrieving academic hold information - " + e.getMessage());
        }
        return PipelineResult.passed();
    }

    // returns the hold description if one exists, null if the student is clear
    private String fetchActiveHold(int studentId, String restrictionType) throws SQLException {
        String sql =
                "SELECT description, amount FROM restrictions " +
                        "WHERE student_id = ? AND restriction_type = ? AND is_active = TRUE";
        return DatabaseManager.getInstance().executeQuery(sql, rs -> {
            if (rs.next()) return rs.getString("description");
            return null;
        }, studentId, restrictionType);
    }
}
