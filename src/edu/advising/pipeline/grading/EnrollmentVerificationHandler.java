package edu.advising.pipeline.grading;

import edu.advising.common.PipelineHandler;
import edu.advising.common.PipelineResult;
import edu.advising.core.DatabaseManager;
import edu.advising.model.Enrollment;

import java.sql.SQLException;
import java.util.Map;

public class EnrollmentVerificationHandler implements PipelineHandler<GradeSubmissionContext> {
    @Override
    public PipelineResult handle(GradeSubmissionContext context) {
        try {
            int studentId = context.getStudent().getId();
            int sectionId = context.getSection().getId();
            Enrollment enrollment = DatabaseManager.getInstance().fetchOne(
                    Enrollment.class,
                    Map.of("student_id", studentId, "section_id", sectionId)
            );
            if(enrollment == null){
                return PipelineResult.failed("ENROLLMENT_VERIFICATION",
                        "Could not verify " +
                        context.getStudent().getFullName() +
                        " is enrolled in " +
                        context.getSection().getCourseName());
            }
            context.setEnrollment(enrollment);
        } catch (SQLException e) {
            return PipelineResult.failed("ENROLLMENT_VERIFICATION",
                    "Error retrieving enrollment record - " +
                    e.getMessage());
        }
        return PipelineResult.passed();
    }
}
