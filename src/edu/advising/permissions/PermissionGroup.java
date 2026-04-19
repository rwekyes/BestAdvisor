package edu.advising.permissions;

import java.util.ArrayList;

public class PermissionGroup implements PermissionComponent{

    private String name;
    private ArrayList<PermissionComponent> children;
    private boolean granted;

    public PermissionGroup(String name){
        this.name = name;
    }

    public void addChild(PermissionComponent c){
        children.add(c);
    }

    public void removeChild(PermissionComponent c){
        children.remove(c);
    }

    public ArrayList<PermissionComponent> getChildren(){
        return children;
    }

    public String getName(){return name;}

    @Override
    public void accept(PermissionVisitor v) {
        v.visit(this);
    }

    @Override
    public String getFeatureCode() {
        return "name";      // getFeatureCode returns the name for branches, in case logic needs it
    }

    public void setGranted(boolean newGranted){
        this.granted = newGranted;
    }

    @Override
    public boolean isGranted() {
        return granted;
    }
}
