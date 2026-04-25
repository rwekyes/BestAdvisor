package edu.advising.catalog;

import java.util.List;

public class CollegeNode implements CatalogNode{

    private String name;
    private List<CatalogNode> departments;

    public CollegeNode(String name, List<CatalogNode> departments){
        this.name = name;
        this.departments = departments;
    }

    @Override
    public String getDisplayName() {
        return name;
    }

    @Override
    public double getTotalCredits() {
        return departments.stream()
                .mapToDouble(d -> d.getTotalCredits())
                .sum();
    }

    @Override
    public int getTotalSections() {
        return departments.stream()
                .mapToInt(d -> d.getTotalSections())
                .sum();
    }

    @Override
    public List<CatalogNode> getChildren() {
        return departments;
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    public void addDepartment(DepartmentNode d){
        departments.add(d);
    }


}
