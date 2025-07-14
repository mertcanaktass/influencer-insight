package com.deneme.influencerinsight.enums;

import lombok.Getter;

import java.util.Arrays;

public class GeneralEnums {

    public enum RoleType {
        ROLE_ADMIN,
        ROLE_CUSTOMER
    }

    @Getter
    public enum Status {
        PASSIVE(0, "passive"),
        ACTIVE(1, "active"),
        DELETED(2, "deleted");

        private Integer id;
        private String status;

        Status(Integer id, String status) {
            this.id = id;
            this.status = status;
        }

        public static String getStatusById(Integer id) {
            return Arrays.stream(Status.values())
                    .filter(s -> s.getId().equals(id))
                    .map(Status::getStatus)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Invalid Status ID: " + id));
        }
    }
}
