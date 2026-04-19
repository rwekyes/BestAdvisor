package edu.advising.permissions;

/*
PermissionTree - The root composite node for the user access profile

 */

import edu.advising.core.DatabaseManager;
import edu.advising.users.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PermissionTree {

    private User user;
    private ArrayList<PermissionComponent> children;

    public PermissionTree(User user) throws SQLException{
        this.user = user;
        DatabaseManager db = DatabaseManager.getInstance();

        // Raw SQL to create a new permission tree from a table join for each session
        String sql = "SELECT p.feature_code, p.can_access, p.role_name " +
                "FROM user_roles ur " +
                "JOIN permissions p ON ur.role_name = p.role_name " +
                "WHERE ur.user_id = ? AND ur.is_active = TRUE";
        // Builds a list of FeaturePermissions from the result set
        List<FeaturePermission> leaves = db.fetchList(sql, rs -> {
                return new FeaturePermission(
                        rs.getString("role_name"), // temporarily store the group name on the leaf
                        rs.getString("feature_code"),
                        rs.getBoolean("can_access"),
                        rs.getString("source")
                );
        }, user.getId());

            // Uses the group name as a key to map the permissions and adds the branches to the list
            // computeIfAbsent is a method in Map that checks the group name and assigns it to the new object if it doesn't already exist in the map
        Map<String, PermissionGroup> groups = new LinkedHashMap<>();

        for (FeaturePermission leaf : leaves) {
                groups
                        .computeIfAbsent(leaf.getGroupName(), name -> new PermissionGroup(name))
                        .addChild(leaf);
        }

        this.children = new ArrayList<>(groups.values());

    }

    public boolean hasPermission(String featureCode){
        for(PermissionComponent component : children){
            if(featureCode.equals(component.getFeatureCode()) && component.isGranted()){
                return true;
            }
        }
        return false;
    }

    public RestrictionSummary getRestrictions(){
        /*
        accepts a visitor and returns the full RestrictionSummary
         */

        return null;
    }

    public String explainDenial(String featureCode){
        return null;
    }

}
