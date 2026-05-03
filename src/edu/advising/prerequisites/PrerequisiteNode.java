package edu.advising.prerequisites;

import java.util.List;

public interface PrerequisiteNode {
    String getDisplayName();
    List<PrerequisiteNode> getChildren();
    boolean isLeaf();
    boolean isCompleted();
}
