package edu.advising.commands;

import edu.advising.model.Enrollment;
import edu.advising.model.Section;
import edu.advising.notifications.ObservableStudent;
import edu.advising.permissions.PermissionTree;

// We're extending the RegistrationContext so we don't need to repeat code
public class DropContext extends RegistrationContext {
    private String dropReason;

    public DropContext(ObservableStudent student, Section section,
                       PermissionTree permissionTree, Enrollment enrollment, String dropReason) {
        super(student, section, permissionTree);
        setEnrollment(enrollment); // enrollment field already exists in parent
        this.dropReason = dropReason;
    }

    public String getDropReason() { return dropReason; }
}