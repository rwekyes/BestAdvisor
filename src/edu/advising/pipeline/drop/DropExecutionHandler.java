package edu.advising.pipeline.drop;

import edu.advising.common.PipelineHandler;
import edu.advising.common.PipelineResult;
import edu.advising.contexts.EnrollmentContext;
import edu.advising.pipeline.registration.RegistrationContext;

import java.sql.SQLException;

public class DropExecutionHandler implements PipelineHandler<RegistrationContext> {
    @Override
    public PipelineResult handle(RegistrationContext ctx) {
        DropContext dropCtx = (DropContext) ctx;
        try {
            EnrollmentContext enrollmentCtx = EnrollmentContext.load(
                    dropCtx.getEnrollment(), dropCtx.getStudent(), dropCtx.getSection());
            enrollmentCtx.drop(dropCtx.getDropReason());
            return PipelineResult.passed();
        } catch (SQLException e) {
            return PipelineResult.failed("DROP_EXECUTION",
                    "Failed to drop " + dropCtx.getSection().getCourseCode() +
                            " — " + e.getMessage());
        }
    }
}