package edu.advising.requirements;

import java.util.List;

public interface RequirementNode {
    String getDisplayName();
    double getTotalCredits();
    double getCompletedCredits();
    double getCompletionPercentage();
    List<RequirementNode> getChildren();
    boolean isLeaf();
}
