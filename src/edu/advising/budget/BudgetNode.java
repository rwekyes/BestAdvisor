package edu.advising.budget;

import java.math.BigDecimal;
import java.util.List;

public interface BudgetNode {
    String getDisplayName();
    BigDecimal getAllocated();
    BigDecimal getSpent();
    BigDecimal getRemaining();
    List<BudgetNode> getChildren();
    boolean isLeaf();
}
