package edu.advising.model;

import edu.advising.core.Column;
import edu.advising.core.Id;
import edu.advising.core.Table;

@Table(name = "permissions")
public class Permissions {
    @Id(isPrimary = true)
    @Column(name = "id", upsertIgnore = true)
    private int id;
    @Column(name = "group_name")
    private String groupName;
    @Column(name = "feature_code")
    private String featureCode;
    @Column(name = "can_access")
    private boolean canAccess;


    public int getId() {
        return id;
    }

    public String getRoleName() {
        return groupName;
    }

    public void setRoleName(String groupName) {
        this.groupName = groupName;
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
