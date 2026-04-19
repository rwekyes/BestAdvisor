package edu.advising.permissions;

/*
AccessDeniedVisitor traverses the tree and returns a human-readable explanation of which restriction is blocking a given feature code and what the student must do to resolve it
 */

public class AccessDeniedVisitor implements PermissionVisitor{
    @Override
    public void visit(FeaturePermission f) {

    }

    @Override
    public void visit(PermissionGroup g) {

    }
}
