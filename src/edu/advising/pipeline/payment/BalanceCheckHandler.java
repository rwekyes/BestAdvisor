package edu.advising.pipeline.payment;

import edu.advising.common.PipelineHandler;
import edu.advising.common.PipelineResult;
import edu.advising.core.DatabaseManager;

import java.math.BigDecimal;
import java.sql.SQLException;

public class BalanceCheckHandler implements PipelineHandler<PaymentContext> {
    @Override
    public PipelineResult handle(PaymentContext context) {
        try{
            BigDecimal balance = getBalance(context.getStudent().getId());
            if(balance.compareTo(BigDecimal.ZERO) <= 0){
                return PipelineResult.failed("BALANCE_CHECK", "Payment not processed - student " +
                                context.getStudent().getFullName() +
                                " has no unpaid balance");
            }
            if(balance.compareTo(context.getAmount()) < 0){
                return PipelineResult.failed("BALANCE_CHECK", "Payment not processed - payment amount " +
                                context.getAmount().toString() +
                                " exceeds balance of " +
                                balance.toString() +
                                " for student " +
                                context.getStudent().getFullName());
            }
        }catch (SQLException e){
            return PipelineResult.failed("BALANCE_CHECK", "Error retrieving balance for "
                    + context.getStudent().getFullName() +
                    " - " + e.getMessage());
        }
        return PipelineResult.passed();
    }

    private BigDecimal getBalance(int studentId) throws SQLException {
        String sql =
                "SELECT " +
                "SUM(CASE WHEN payment_type = 'CHARGE' THEN amount ELSE 0 END) - " +
                "SUM(CASE WHEN status = 'COMPLETED' THEN amount ELSE 0 END) " +
                "AS balance " +
                "FROM payments " +
                "WHERE student_id = ?";
        return DatabaseManager.getInstance().executeQuery(sql, rs -> {
            BigDecimal balance = rs.getBigDecimal("balance");
            return balance != null ? balance : BigDecimal.ZERO;
        }, studentId);
    }
}
