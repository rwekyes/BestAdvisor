package edu.advising.pipeline.waitlist;

import edu.advising.common.PipelineHandler;
import edu.advising.common.PipelineResult;
import edu.advising.pipeline.registration.RegistrationContext;

public class WaitlistAddHandler implements PipelineHandler<RegistrationContext> {
    @Override
    public PipelineResult handle(RegistrationContext ctx) {
        int waitlistId = ctx.getSection().addToWaitlist(ctx.getStudent());
        if (waitlistId == 0) {
            return PipelineResult.failed("WAITLIST_ADD",
                    "Failed to add to waitlist for " + ctx.getSection().getCourseCode());
        }
        return PipelineResult.passed();
    }
}
