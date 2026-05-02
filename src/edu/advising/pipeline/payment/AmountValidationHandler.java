package edu.advising.pipeline.payment;

import edu.advising.common.PipelineHandler;
import edu.advising.common.PipelineResult;

import java.math.BigDecimal;

public class AmountValidationHandler implements PipelineHandler<PaymentContext> {
    @Override
    public PipelineResult handle(PaymentContext context) {
        if(context.getAmount() == null){
            return PipelineResult.failed("AMOUNT_VALIDATION_CHECK",
                    "Payment amount must be greater than zero");
        }
        if(context.getAmount().compareTo(BigDecimal.ZERO) <= 0){
            return PipelineResult.failed("AMOUNT_VALIDATION_CHECK", context.getAmount().toString() +
                    " - is not a valid payment amount. Payment amount must be greater than zero.");
        }
        return PipelineResult.passed();
    }
}
