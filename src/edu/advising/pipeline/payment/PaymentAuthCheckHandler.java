package edu.advising.pipeline.payment;

import edu.advising.common.PipelineHandler;
import edu.advising.common.PipelineResult;

public class PaymentAuthCheckHandler implements PipelineHandler<PaymentContext> {
    @Override
    public PipelineResult handle(PaymentContext context) {
        if (context.getAuthenticationResult() == null || !context.getAuthenticationResult().isFullyAuthenticated()) {
            return PipelineResult.failed("AUTH_CHECK",
                    "You must be logged in to make a payment.");
        }
        return PipelineResult.passed();
    }
}
