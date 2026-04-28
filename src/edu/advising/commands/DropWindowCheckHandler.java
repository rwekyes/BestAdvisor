package edu.advising.commands;

import edu.advising.common.PipelineHandler;
import edu.advising.common.PipelineResult;
import edu.advising.contexts.EnrollmentContext;

public class DropWindowCheckHandler implements PipelineHandler<RegistrationContext> {
    @Override
    public PipelineResult handle(RegistrationContext ctx) {
        EnrollmentContext enrollmentCtx = EnrollmentContext.load(
                ctx.getEnrollment(), ctx.getStudent(), ctx.getSection());
        if (!enrollmentCtx.canDrop()) {
            String status = ctx.getEnrollment().getStatus();
            return PipelineResult.failed("DROP_WINDOW_CHECK",
                    "Cannot drop " + ctx.getSection().getCourseCode() +
                            " — enrollment is currently " + status);
        }
        return PipelineResult.passed();
    }
}
