package edu.advising.permissions;

/*
PermissionCheckVisitor - Traverses the permission tree and returns true or false for each feature depending on the user
 */

public class PermissionCheckVisitor implements PermissionVisitor{

    private String featureCode;
    private boolean result = false;

    public PermissionCheckVisitor(String featureCode){
        this.featureCode = featureCode;
    }

    @Override
    public void visit(FeaturePermission f) {
        if(f.getFeatureCode().equals(featureCode) && f.isGranted()){
            result = true;
        }
    }

    @Override
    public void visit(PermissionGroup g) {
        for(PermissionComponent child : g.getChildren()){
            child.accept(this);
        }
    }

    public String getFeatureCode(){
        return featureCode;     // getter for debugging purposes
    }

    public boolean getResult(){
        return result;
    }

}
