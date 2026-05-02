package edu.advising.pipeline.payment;

import edu.advising.common.PipelineHandler;
import edu.advising.common.PipelineResult;
import edu.advising.core.DatabaseManager;
import edu.advising.model.Payment;

import java.sql.SQLException;
import java.time.LocalDateTime;

public class DuplicatePaymentCheckHandler implements PipelineHandler<PaymentContext> {
    @Override
    public PipelineResult handle(PaymentContext context) {
        try {
            if (isRecentPayment(context.getStudent().getId(), context.getPaymentType(), LocalDateTime.now().minusMinutes(5))) {
                return PipelineResult.failed("DUPLICATE_PAYMENT_CHECK",
                        "Duplicate payment detected within the last 5 minutes.");
            }
        } catch (SQLException e) {
            return PipelineResult.failed("DUPLICATE_PAYMENT_CHECK",
                    "Error retrieving payment data - " + e.getMessage());
        }
        return PipelineResult.passed();
    }

    private boolean isRecentPayment(int studentId, String paymentType, LocalDateTime time) throws SQLException {
        String sql =
                "SELECT * FROM payments "+
                "WHERE student_id = ? " +
                "AND payment_type = ? " +
                "AND status IN ('PENDING', 'COMPLETED')" +
                "AND payment_date > ?";
        Boolean result = DatabaseManager.getInstance().executeQuery(sql,rs ->{
            if(rs.next()) return true;
            return false;
        }, studentId, paymentType, time);
        return result != null && result;
    }
}
