package edu.advising.catalog;

import edu.advising.commands.Department;

import java.util.List;

public class DepartmentNode implements CatalogNode{

    private Department department;

    private List<CatalogNode> courses;

    @Override
    public String getDisplayName() {
        return department.getName();
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
        return courses;
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    public Department getDepartment() {
        return department;
    }

    public void addCourse(CourseNode c){
        courses.add(c);
    }


}
