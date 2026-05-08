package edu.advising.pipeline.drop;

import edu.advising.auth.AuthenticationResult;
import edu.advising.model.Enrollment;
import edu.advising.model.Section;
import edu.advising.notifications.ObservableStudent;
import edu.advising.permissions.PermissionTree;
import edu.advising.pipeline.registration.RegistrationContext;

// We're extending the RegistrationContext so we don't need to repeat code
public class DropContext extends RegistrationContext {
    private String dropReason;

    public DropContext(ObservableStudent student, Section section,
                       PermissionTree permissionTree, Enrollment enrollment, String dropReason) {
        super(student, section, permissionTree, AuthenticationResult.success(student));
        setEnrollment(enrollment); // enrollment field already exists in parent
        this.dropReason = dropReason;
    }

    public String getDropReason() { return dropReason; }
}