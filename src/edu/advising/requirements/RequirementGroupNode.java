package edu.advising.requirements;

import java.util.List;

public class RequirementGroupNode implements RequirementNode{

    private String name;
    private List<RequirementNode> children;

    public RequirementGroupNode(List<RequirementNode> children, String name){
        this.children = children;
        this.name = name;
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
    public double getCompletedCredits() {
        return children.stream()
                .mapToDouble(d -> d.getCompletedCredits())
                .sum();
    }

    @Override
    public double getCompletionPercentage() {
        double total = getTotalCredits();
        return total == 0 ? 0.0 : getCompletedCredits() / total;
    }

    @Override
    public List<RequirementNode> getChildren() {
        return children;
    }

    @Override
    public boolean isLeaf() {
        return false;
    }
}
