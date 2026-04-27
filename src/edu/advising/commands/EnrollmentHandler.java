package edu.advising.commands;

import edu.advising.common.PipelineHandler;
import edu.advising.common.PipelineResult;
import edu.advising.contexts.EnrollmentContext;

import java.sql.SQLException;

public class EnrollmentHandler implements PipelineHandler<RegistrationContext> {
    @Override
    public PipelineResult handle(RegistrationContext ctx) {
        try {
            EnrollmentContext enrollmentContext = EnrollmentContext.create(ctx.getStudent(), ctx.getSection());

            enrollmentContext.confirm(); // PENDING → ENROLLED, persists, notifies

            ctx.setEnrollment(enrollmentContext.getEnrollment());

            return PipelineResult.passed();

        } catch (SQLException | IllegalAccessException e) {
            return PipelineResult.failed("ENROLLMENT", "Error in during enrollment for " +
                    ctx.getSection().getCourseCode() + " - " + e.getMessage());
        }
    }
}
