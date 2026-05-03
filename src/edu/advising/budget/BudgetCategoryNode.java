package edu.advising.budget;

import java.math.BigDecimal;
import java.util.List;

public class BudgetCategoryNode implements BudgetNode{

    private String name;
    private List<BudgetNode> children;

    public BudgetCategoryNode(String name, List<BudgetNode> children){
        this.name = name;
        this.children = children;
    }

    @Override
    public String getDisplayName() {
        return name;
    }

    @Override
    public BigDecimal getAllocated() {
        return children.stream()
                .map(BudgetNode::getAllocated)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal getSpent() {
        return children.stream()
                .map(BudgetNode::getSpent)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal getRemaining() {
        return getAllocated().subtract(getSpent());
    }

    @Override
    public List<BudgetNode> getChildren() {
        return children;
    }

    @Override
    public boolean isLeaf() {
        return false;
    }
}
