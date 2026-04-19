package edu.advising.permissions;

import edu.advising.core.Column;
import edu.advising.core.Id;
import edu.advising.core.Table;

@Table(name = "permissions")
public class Permissions {
    @Id(isPrimary = true)
    @Column(name = "id", upsertIgnore = true)
    private int id;
    @Column(name = "role_name")
    private String roleName;
    @Column(name = "feature_code")
    private String featureCode;
    @Column(name = "can_access")
    private boolean canAccess;


    public int getId() {
        return id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getFeatureCode() {
        return featureCode;
    }

    public void setFeatureCode(String featureCode) {
        this.featureCode = featureCode;
    }

    public boolean isCanAccess() {
        return canAccess;
    }

    public void setCanAccess(boolean canAccess) {
        this.canAccess = canAccess;
    }
}
