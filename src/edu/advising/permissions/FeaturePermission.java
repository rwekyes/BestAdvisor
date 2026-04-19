package edu.advising.permissions;

public class FeaturePermission implements PermissionComponent{
    private String featureCode;
    private boolean granted;
    private String groupName;
    private String source;
    private String description;

    public FeaturePermission(String groupName, String featureCode, boolean granted, String source){
        this.groupName = groupName;
        this.featureCode = featureCode;
        this.granted = granted;
        this.source = source;
    }

    @Override
    public boolean isGranted(){
        return granted;
    }

    @Override
    public void accept(PermissionVisitor v) {
        v.visit(this);
    }

    public String getGroupName() {
        return groupName;
    }

    public String getSource() {
        return source;
    }

    @Override
    public String getFeatureCode() {
        return featureCode;
    }

    public String getDescription(){
        return description;
    }
}
