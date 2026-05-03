package edu.advising.plan;

import java.util.List;

public interface PlanNode {
    String getDisplayName();
    double getTotalCredits();
    List<PlanNode> getChildren();
    boolean isLeaf();
}
