package edu.advising.pipeline.grading;

import edu.advising.auth.AuthenticationResult;
import edu.advising.model.Enrollment;
import edu.advising.model.Section;
import edu.advising.notifications.ObservableStudent;
import edu.advising.users.Faculty;

public class GradeSubmissionContext {
    private Faculty faculty;
    private ObservableStudent student;
    private Section section;
    private String grade;
    private Enrollment enrollment;
    private AuthenticationResult authenticationResult;

    public GradeSubmissionContext(Faculty faculty, ObservableStudent student, Section section, String grade, AuthenticationResult authenticationResult){
        this.faculty = faculty;
        this.student = student;
        this.section = section;
        this.grade = grade;
        this.authenticationResult = authenticationResult;
    }

    public Faculty getFaculty() {
        return faculty;
    }

    public ObservableStudent getStudent() {
        return student;
    }

    public Section getSection() {
        return section;
    }

    public String getGrade() {
        return grade;
    }

    public Enrollment getEnrollment() {
        return enrollment;
    }

    public void setEnrollment(Enrollment enrollment) {
        this.enrollment = enrollment;
    }

    public AuthenticationResult getAuthenticationResult() {
        return authenticationResult;
    }
}
