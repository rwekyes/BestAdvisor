package edu.advising.pipeline.payment;

import edu.advising.common.PipelineHandler;
import edu.advising.common.PipelineResult;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class PaymentMethodValidationHandler implements PipelineHandler<PaymentContext> {
    @Override
    public PipelineResult handle(PaymentContext context) {
        if (!"CREDIT_CARD".equals(context.getPaymentMethod())) return PipelineResult.passed();

        if(context.getCardNumber().length() != 16){
            return PipelineResult.failed("PAYMENT_METHOD_VALIDATION",
                    "Card number is not 16 digits.");
        }

        try {
            if (YearMonth.parse(context.getExpiryDate(), DateTimeFormatter.ofPattern("MM/yy")).isBefore(YearMonth.now())) {
                return PipelineResult.failed("PAYMENT_METHOD_VALIDATION", "Card is expired.");
            }
        } catch (DateTimeParseException e) {
            return PipelineResult.failed("PAYMENT_METHOD_VALIDATION", "Expiry date must be in MM/YY format.");
        }

        if(context.getCvv().length() != 3){
            return PipelineResult.failed("PAYMENT_METHOD_VALIDATION",
                    "Card number cvv number is invalid");
        }

        return PipelineResult.passed();
    }
}
