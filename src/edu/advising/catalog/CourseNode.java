package edu.advising.catalog;

import edu.advising.model.Course;

import java.util.List;

public class CourseNode implements CatalogNode{

    private Course course;
    private List<CatalogNode> sections;

    public CourseNode(Course course, List<CatalogNode> sections){
        this.course = course;
        this.sections = sections;
    }

    @Override
    public String getDisplayName() {
        return course.getName();
    }

    @Override
    public double getTotalCredits() {
        return course.getCredits() * sections.size();
    }

    @Override
    public int getTotalSections() {
        return sections.size();
    }

    @Override
    public List<CatalogNode> getChildren() {
        return sections;
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    public Course getCourse() {
        return course;
    }

    public void addSection(SectionNode s){
        sections.add(s);
    }
}
