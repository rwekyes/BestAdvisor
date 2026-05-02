package edu.advising.pipeline.payment;

import edu.advising.common.PipelineHandler;
import edu.advising.common.PipelineResult;
import edu.advising.core.DatabaseManager;
import edu.advising.model.Payment;

import java.sql.SQLException;

public class PaymentProcessingHandler implements PipelineHandler<PaymentContext> {
    @Override
    public PipelineResult handle(PaymentContext context) {
            Payment payment = new Payment(context.getStudent().getId(),
                    context.getAmount(), context.getPaymentType(), context.getPaymentMethod(),
                    "PENDING");

                // Call to the payment gateway would go here, but we don't have one so it just moves on to completed

            payment.setStatus("COMPLETED"); // Only gets ran if payment was successful
            payment.setTransactionId("TestTransactionId - "+System.currentTimeMillis()); // Fake Id for now

        try{
            DatabaseManager.getInstance().upsert(payment);
            context.setPayment(payment);
        }catch (SQLException | IllegalAccessException e){ // Fine for the project, but in a real system would handle payment negation
            return PipelineResult.failed("PAYMENT_PROCESSING", "Error processing payment - " + e.getMessage());
        }
        return PipelineResult.passed();
    }
}
