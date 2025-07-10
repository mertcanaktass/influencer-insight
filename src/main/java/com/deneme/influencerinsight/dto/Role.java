package com.deneme.influencerinsight.dto;

import com.deneme.influencerinsight.enums.RoleType;

public class Role {

    private String roleName;
    private RoleType roleType;

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public RoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }
}
