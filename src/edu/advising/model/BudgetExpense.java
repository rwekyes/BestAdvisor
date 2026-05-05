package edu.advising.model;

import edu.advising.core.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table(name = "budget_expenses")
public class BudgetExpense {
    @Id(isPrimary = true)
    @Column(name = "id", upsertIgnore = true)
    private int id;

    @Column(name = "budget_id", foreignKey = true)
    private int budgetId;

    @Column(name = "description")
    private String description;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "expense_date")
    private LocalDate expenseDate;

    @Column(name = "category")
    private String category;

    @Column(name = "receipt_number")
    private String receiptNumber;

    @Column(name = "approved_by", nullableforeignKey = true)
    private int approvedBy;         // FK → faculty(id)

    public BudgetExpense() {}

    public BudgetExpense(int budgetId, String description, BigDecimal amount, LocalDate expenseDate) {
        this.budgetId = budgetId;
        this.description = description;
        this.amount = amount;
        this.expenseDate = expenseDate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getBudgetId() { return budgetId; }
    public void setBudgetId(int budgetId) { this.budgetId = budgetId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDate getExpenseDate() { return expenseDate; }
    public void setExpenseDate(LocalDate expenseDate) { this.expenseDate = expenseDate; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getReceiptNumber() { return receiptNumber; }
    public void setReceiptNumber(String receiptNumber) { this.receiptNumber = receiptNumber; }

    public int getApprovedBy() { return approvedBy; }
    public void setApprovedBy(int approvedBy) { this.approvedBy = approvedBy; }
}
