package edu.advising.catalog;

import edu.advising.commands.Department;

import java.util.List;

public class CollegeNode implements CatalogNode{

    private String name;
    private List<CatalogNode> departments;

    @Override
    public String getDisplayName() {
        return name;
    }

    @Override
    public double getTotalCredits() {
        return 0;
    }

    @Override
    public int getTotalSections() {
        return 0;
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
