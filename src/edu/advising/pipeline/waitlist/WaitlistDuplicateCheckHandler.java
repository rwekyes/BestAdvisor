package edu.advising.pipeline.waitlist;

import edu.advising.common.PipelineHandler;
import edu.advising.common.PipelineResult;
import edu.advising.pipeline.registration.RegistrationContext;

import java.sql.SQLException;

public class WaitlistDuplicateCheckHandler implements PipelineHandler<RegistrationContext> {
    @Override
    public PipelineResult handle(RegistrationContext ctx) {
        try {
            boolean alreadyWaiting = ctx.getSection().getWaitlist().stream()
                    .anyMatch(we -> we.getStudentId() == ctx.getStudent().getId());
            if (alreadyWaiting) {
                return PipelineResult.failed("WAITLIST_DUPLICATE_CHECK",
                        "Already on waitlist for " + ctx.getSection().getCourseCode());
            }
        } catch (SQLException e) {
            return PipelineResult.failed("WAITLIST_DUPLICATE_CHECK",
                    "Could not verify waitlist status for " + ctx.getSection().getCourseCode());
        }
        return PipelineResult.passed();
    }
}
