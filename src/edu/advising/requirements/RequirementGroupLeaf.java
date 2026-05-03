package edu.advising.requirements;

import edu.advising.model.Course;

import java.util.List;

public class RequirementGroupLeaf implements RequirementNode{

    private String name;
    private Course course;
    private boolean completed;

    public RequirementGroupLeaf(Course course, String name, boolean completed){
        this.course = course;
        this.name = name;
        this.completed = completed;
    }

    @Override
    public String getDisplayName() {
        return name;
    }

    @Override
    public double getTotalCredits() {
        return course.getCredits();
    }

    @Override
    public double getCompletedCredits() {
        if(isCompleted()){
            return getTotalCredits();
        }
        return 0;
    }

    @Override
    public double getCompletionPercentage() {
        return completed ? 1.0 : 0.0;
    }

    @Override
    public List<RequirementNode> getChildren() {
        return null;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
