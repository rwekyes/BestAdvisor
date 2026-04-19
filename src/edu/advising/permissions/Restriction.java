package edu.advising.permissions;

public class Restriction {
    public final String type;
    public final String office;
    public final String resolution;

    public Restriction(FeaturePermission fp){
        this.type = fp.getFeatureCode();
        this.office = fp.getGroupName();
        this.resolution = fp.getSource();
    }
}
