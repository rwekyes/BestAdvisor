package edu.advising.permissions;

public interface PermissionComponent {
    public void accept(PermissionVisitor v);
    public String getFeatureCode();
    public boolean isGranted();
    public void setGranted(boolean newGranted);
}
