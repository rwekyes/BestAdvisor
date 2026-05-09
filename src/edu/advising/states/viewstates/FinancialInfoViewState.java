package edu.advising.states.viewstates;

import edu.advising.contexts.ViewContext;
import edu.advising.core.DatabaseManager;
import edu.advising.model.Payment;
import edu.advising.states.ViewState;
import edu.advising.users.Student;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class FinancialInfoViewState implements ViewState {

    private static final FinancialInfoViewState instance = new FinancialInfoViewState();

    @Override public boolean requiresAuthentication() { return true; }

    @Override
    public void handleAction(ViewContext ctx, String command, String... params) {
        switch (command) {
            case "LOGOUT" -> ctx.logout();
            default -> throw new IllegalArgumentException("Unknown command: " + command);
        }
    }

    @Override public void enter(ViewContext ctx) {}
    @Override public void exit(ViewContext ctx)  {}

    @Override
    public void render(ViewContext viewContext) {
        Student student = (Student) viewContext.getCurrentUser();
        System.out.println();
        System.out.printf("  ---- Financial Information: %s ----%n", student.getFullName());
        System.out.println();

        try {
            List<Payment> payments = DatabaseManager.getInstance()
                    .fetchMany(Payment.class, "student_id", student.getId());

            if (payments.isEmpty()) {
                System.out.println("  No payment records found.");
            } else {
                System.out.printf("  %-14s  %-8s  %-14s  %-12s  %-12s  %s%n",
                        "Reference", "Amount", "Type", "Method", "Status", "Date");
                System.out.println("  " + "-".repeat(82));
                BigDecimal total = BigDecimal.ZERO;
                for (Payment p : payments) {
                    String ref    = p.getReferenceNumber() != null ? p.getReferenceNumber() : "—";
                    String amt    = p.getAmount() != null ? "$" + p.getAmount().toPlainString() : "—";
                    String type   = p.getPaymentType()   != null ? p.getPaymentType()   : "—";
                    String method = p.getPaymentMethod() != null ? p.getPaymentMethod() : "—";
                    String date   = p.getPaymentDate()   != null ? p.getPaymentDate().toLocalDate().toString() : "—";
                    System.out.printf("  %-14s  %-8s  %-14s  %-12s  %-12s  %s%n",
                            ref.length() > 14 ? ref.substring(0, 14) : ref,
                            amt, type, method, p.getStatus(), date);
                    if (p.getAmount() != null && "COMPLETED".equals(p.getStatus())) {
                        total = total.add(p.getAmount());
                    }
                }
                System.out.println("  " + "-".repeat(82));
                System.out.printf("  Total Paid: $%s%n", total.toPlainString());
            }
        } catch (SQLException ex) {
            System.out.println("  (Could not load payment records — " + ex.getMessage() + ")");
        }

        System.out.println();
        System.out.println("  Shortcuts: [b] Back   [l] Logout   [q] Quit");
    }

    @Override public String getViewName() { return "FINANCIAL_INFO"; }
    public static FinancialInfoViewState getInstance() { return instance; }
}
