package edu.advising.permissions;

/*
traverses the tree and collects all active holds with type, description, placing office, and resolution instructions into a RestrictionSummary
 */


public class RestrictionListVisitor implements PermissionVisitor{

    private RestrictionSummary summary;

    @Override
    public void visit(FeaturePermission f) {
        if(!f.isGranted()) summary.add(new Restriction(f));
    }

    @Override
    public void visit(PermissionGroup g) {

    }

    public RestrictionSummary getSummary(){
        return summary;
    }
}
