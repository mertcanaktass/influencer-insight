package com.deneme.influencerinsight.enums;

import lombok.Getter;

import java.util.Arrays;

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

    public static Status getStatusById(Integer id) {
        return Arrays.stream(Status.values())
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid Status ID: " + id));
    }

    public static Status getStatusByShortCode(String shortCode) {
        return Arrays.stream(Status.values())
                .filter(s -> s.getStatus().equals(shortCode))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid Status Short Code: " + shortCode));
    }

    public static Integer getIdByStatus(String status) {
        return Arrays.stream(Status.values())
                .filter(s -> s.getStatus().equalsIgnoreCase(status))
                .map(Status::getId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid Status: " + status));
    }
}
