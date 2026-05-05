package edu.advising.model;

import edu.advising.core.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "budgets")
public class Budget {
    @Id(isPrimary = true)
    @Column(name = "id", upsertIgnore = true)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "parent_budget_id", nullableforeignKey = true)
    private int parentBudgetId;

    @Column(name = "budget_type")
    private String budgetType;      // DEPARTMENT, RESEARCH, TEACHING, etc.

    @Column(name = "owner_id", nullableforeignKey = true)
    private int ownerId;            // FK → faculty(id)

    @Column(name = "fiscal_year")
    private String fiscalYear;

    @Column(name = "allocated_amount")
    private BigDecimal allocatedAmount;

    @Column(name = "spent_amount")
    private BigDecimal spentAmount;

    @Column(name = "created_at", upsertIgnore = true)
    private LocalDateTime createdAt;

    public Budget() {}

    public Budget(String name, String budgetType, int ownerId,
                  String fiscalYear, BigDecimal allocatedAmount) {
        this.name = name;
        this.budgetType = budgetType;
        this.ownerId = ownerId;
        this.fiscalYear = fiscalYear;
        this.allocatedAmount = allocatedAmount;
        this.spentAmount = BigDecimal.ZERO;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getParentBudgetId() { return parentBudgetId; }
    public void setParentBudgetId(int parentBudgetId) { this.parentBudgetId = parentBudgetId; }

    public String getBudgetType() { return budgetType; }
    public void setBudgetType(String budgetType) { this.budgetType = budgetType; }

    public int getOwnerId() { return ownerId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }

    public String getFiscalYear() { return fiscalYear; }
    public void setFiscalYear(String fiscalYear) { this.fiscalYear = fiscalYear; }

    public BigDecimal getAllocatedAmount() { return allocatedAmount; }
    public void setAllocatedAmount(BigDecimal allocatedAmount) { this.allocatedAmount = allocatedAmount; }

    public BigDecimal getSpentAmount() { return spentAmount; }
    public void setSpentAmount(BigDecimal spentAmount) { this.spentAmount = spentAmount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
