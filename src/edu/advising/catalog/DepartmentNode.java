package edu.advising.catalog;

import edu.advising.model.Course;
import edu.advising.model.Department;

import java.util.List;

public class DepartmentNode implements CatalogNode{

    private Department department;
    private List<CatalogNode> courses;

    public DepartmentNode(Department department, List<CatalogNode> courses){
        this.department = department;
        this.courses = courses;
    }

    @Override
    public String getDisplayName() {
        return department.getName();
    }

    @Override
    public double getTotalCredits() {
        return courses.stream()
                .mapToDouble(c -> c.getTotalCredits())
                .sum();
    }

    @Override
    public int getTotalSections() {
        return courses.stream()
                .mapToInt(c -> c.getTotalSections())
                .sum();
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
