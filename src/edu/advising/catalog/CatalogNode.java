package edu.advising.catalog;

import java.util.List;

public interface CatalogNode {
    public String getDisplayName();
    public double getTotalCredits();
    public int getTotalSections();
    public List<CatalogNode> getChildren();
    public boolean isLeaf();
}
