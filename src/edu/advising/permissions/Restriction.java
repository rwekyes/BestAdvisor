package edu.advising.permissions;

public class Restriction {
    public final String type;
    public final String office;
    public final String resolution;

    public Restriction(FeaturePermission fp, String office){
        this.type = fp.getFeatureCode();
        this.office = office;
        this.resolution = fp.getSource();
    }
}
