package edu.advising.budget;

import edu.advising.model.BudgetExpense;

import java.math.BigDecimal;
import java.util.List;

public class BudgetLineItemNode implements BudgetNode{

    private BudgetExpense expense;


    public BudgetLineItemNode(String name, BudgetExpense expense){
        this.expense = expense;
    }

    @Override
    public String getDisplayName() {
        return expense.getDescription();
    }

    @Override
    public BigDecimal getAllocated() {
        return expense.getAmount();
    }

    @Override
    public BigDecimal getSpent() {
        return expense.getAmount();
    }

    @Override
    public BigDecimal getRemaining() {
        return BigDecimal.ZERO;
    }

    @Override
    public List<BudgetNode> getChildren() {
        return null;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }
}
