package edu.advising.pipeline.registration;

import edu.advising.common.PipelineHandler;
import edu.advising.common.PipelineResult;

public class AuthCheckHandler implements PipelineHandler<RegistrationContext> {
    @Override
    public PipelineResult handle(RegistrationContext context) {
        if (context.getAuthenticationResult() == null || !context.getAuthenticationResult().isFullyAuthenticated()) {
            return PipelineResult.failed("AUTH_CHECK",
                    "You must be logged in to register for classes.");
        }
        return PipelineResult.passed();
    }
}
