package edu.advising.plan;

import edu.advising.model.Course;

import java.util.List;

public class PlannedCourseNode implements PlanNode{

    private Course course;

    public PlannedCourseNode(Course course){
        this.course = course;
    }

    @Override
    public String getDisplayName() {
        return course.getName();
    }

    @Override
    public double getTotalCredits() {
        return course.getCredits();
    }

    @Override
    public List<PlanNode> getChildren() {
        return null;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }
}
