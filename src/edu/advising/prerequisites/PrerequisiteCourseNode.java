package edu.advising.prerequisites;

import edu.advising.core.DatabaseManager;
import edu.advising.model.Course;
import edu.advising.users.Student;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PrerequisiteCourseNode implements PrerequisiteNode{

    private Course course;
    private boolean completed;
    private List<PrerequisiteNode> children;

    private PrerequisiteCourseNode(Course course, boolean completed, List<PrerequisiteNode> children){
        this.course = course;
        this.completed = completed;
        this.children = children;
    }

    public static PrerequisiteCourseNode build(Course course, Student student) throws SQLException {
        boolean completed = hasStudentCompleted(course, student);
        List<Course> prereqs = fetchPrerequisites(course);
        List<PrerequisiteNode> children = new ArrayList<>();
        for (Course prereq : prereqs) {
            children.add(build(prereq, student));
        }
        return new PrerequisiteCourseNode(course, completed, children);
    }

    @Override
    public String getDisplayName() {
        return course.getName();
    }

    @Override
    public List<PrerequisiteNode> getChildren() {
        return children;
    }

    @Override
    public boolean isLeaf() {
        return children.isEmpty();
    }

    @Override
    public boolean isCompleted() {
        return completed;
    }

    private static boolean hasStudentCompleted(Course course, Student student) throws SQLException{
        String sql = "SELECT 1 FROM enrollments " +
                "WHERE student_id = ? " +
                "AND section_id " +
                "IN (SELECT id FROM sections WHERE course_id = ?) " +
                "AND status = 'COMPLETED'";
        return DatabaseManager.getInstance().executeQuery(sql, ResultSet::next,
                student.getId(), course.getId());
    }

    private static List<Course> fetchPrerequisites(Course course) throws SQLException{
        return course.getPrerequisites();
    }
}
