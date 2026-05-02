package edu.advising.pipeline.grading;

import edu.advising.common.PipelineHandler;
import edu.advising.common.PipelineResult;
import edu.advising.core.DatabaseManager;
import edu.advising.model.Enrollment;

import java.sql.SQLException;
import java.time.LocalDateTime;

public class GradeSubmissionHandler implements PipelineHandler<GradeSubmissionContext> {
    @Override
    public PipelineResult handle(GradeSubmissionContext context) {
        try {
            Enrollment enrollment = context.getEnrollment();
            enrollment.setGrade(context.getGrade());
            enrollment.setFinalGrade(context.getGrade());
            enrollment.setGradedAt(LocalDateTime.now());
            enrollment.setStatus("COMPLETED");
            DatabaseManager.getInstance().upsert(enrollment);
        }catch(SQLException | IllegalAccessException e){
            return PipelineResult.failed("GRADE_SUBMISSION",
                    "Error updating grade for " +
                    context.getStudent().getFullName() +
                    " - " + e.getMessage());
        }
        return PipelineResult.passed();
    }
}
