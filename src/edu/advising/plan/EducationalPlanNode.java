package edu.advising.plan;

import java.util.List;

public class EducationalPlanNode implements PlanNode{

    private String name;
    private List<PlanNode> children;

    public EducationalPlanNode(String name, List<PlanNode> children){
        this.name = name;
        this.children = children;
    }

    @Override
    public String getDisplayName() {
        return name;
    }

    @Override
    public double getTotalCredits() {
        return children.stream()
                .mapToDouble(d -> d.getTotalCredits())
                .sum();
    }

    @Override
    public List<PlanNode> getChildren() {
        return children;
    }

    @Override
    public boolean isLeaf() {
        return false;
    }
}
