package edu.advising.catalog;

import java.util.List;

public interface CatalogNode {
    String getDisplayName();
    double getTotalCredits();
    int getTotalSections();
    List<CatalogNode> getChildren();
    boolean isLeaf();
}
