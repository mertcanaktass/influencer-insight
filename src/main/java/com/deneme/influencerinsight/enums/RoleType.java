package com.deneme.influencerinsight.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum RoleType {
    ROLE_ADMIN(1L, "ADMIN"),
    ROLE_CUSTOMER(2L, "CUSTOMER");

    private final Long id;
    private final String type;

    RoleType(Long id, String type) {
        this.id = id;
        this.type = type;
    }

    public static RoleType getRoleTypeById(Long roleId) {
        return Arrays.stream(RoleType.values())
                .filter(role -> role.getId().equals(roleId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid Role ID: " + roleId));
    }
}