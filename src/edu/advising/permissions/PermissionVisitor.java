package edu.advising.permissions;

public interface PermissionVisitor {

    public void visit(FeaturePermission f);
    public void visit(PermissionGroup g);

}
