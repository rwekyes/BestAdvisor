package edu.advising.pipeline.registration;

import edu.advising.auth.AuthenticationContext;
import edu.advising.auth.AuthenticationResult;
import edu.advising.model.Enrollment;
import edu.advising.model.Section;
import edu.advising.notifications.ObservableStudent;
import edu.advising.permissions.PermissionTree;

public class RegistrationContext {
    private ObservableStudent student;
    private Section section;
    private PermissionTree permissionTree;
    private Enrollment enrollment;
    private AuthenticationResult authenticationResult;

    public RegistrationContext(ObservableStudent student, Section section, PermissionTree permissionTree, AuthenticationResult authenticationResult){
        this.student = student;
        this.section = section;
        this.permissionTree = permissionTree;
        this.authenticationResult = authenticationResult;
    }

    public ObservableStudent getStudent() {
        return student;
    }

    public Section getSection() {
        return section;
    }

    public PermissionTree getPermissionTree() {
        return permissionTree;
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
