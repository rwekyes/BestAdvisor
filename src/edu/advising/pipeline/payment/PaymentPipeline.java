package edu.advising.pipeline.payment;

import edu.advising.common.PipelineHandler;
import edu.advising.common.PipelineResult;

import java.util.List;

public class PaymentPipeline {
    private final List<PipelineHandler<PaymentContext>> handlers;

    public PaymentPipeline(List<PipelineHandler<PaymentContext>> handlers){
        this.handlers = handlers;
    }

    public static PaymentPipeline standard() {
        return new PaymentPipeline(List.of(
                new PaymentAuthCheckHandler(),
                new PaymentMethodValidationHandler(),
                new AmountValidationHandler(),
                new BalanceCheckHandler(),
                new DuplicatePaymentCheckHandler(),
                new PaymentProcessingHandler()
        ));
    }

    public PipelineResult run(PaymentContext ctx){
        for (PipelineHandler<PaymentContext> handler : handlers){
            PipelineResult result = handler.handle(ctx);
            if (!result.isPassed()) return result;
        }
        return PipelineResult.passed();
    }
}
