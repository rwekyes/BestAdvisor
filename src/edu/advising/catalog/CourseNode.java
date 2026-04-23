package edu.advising.catalog;

import edu.advising.commands.Course;
import edu.advising.commands.Section;

import java.util.List;

public class CourseNode implements CatalogNode{

    private Course course;

    private List<CatalogNode> sections;

    @Override
    public String getDisplayName() {
        return course.getName();
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
