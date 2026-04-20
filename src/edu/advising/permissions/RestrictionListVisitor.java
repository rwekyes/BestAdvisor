package edu.advising.permissions;

/*
traverses the tree and collects all active holds with type, description, placing office, and resolution instructions into a RestrictionSummary
 */


public class RestrictionListVisitor implements PermissionVisitor{

    private RestrictionSummary summary = new RestrictionSummary();

    @Override
    public void visit(FeaturePermission f) {
        if(!f.isGranted() && isHoldGroup(f.getGroupName())) {
            String office = PermissionTreeFactory.toOffice(f.getGroupName());
            summary.add(new Restriction(f, office));
        }
    }

    @Override
    public void visit(PermissionGroup g) {
        for(PermissionComponent child : g.getChildren()){
            child.accept(this);
        }
    }

    private boolean isHoldGroup(String groupName){
        return  groupName.equals(GroupNames.FINANCIAL_HOLD) ||
                groupName.equals(GroupNames.ACADEMIC_HOLD) ||
                groupName.equals(GroupNames.LIBRARY_HOLD);
    }

    public RestrictionSummary getSummary(){
        return summary;
    }
}
