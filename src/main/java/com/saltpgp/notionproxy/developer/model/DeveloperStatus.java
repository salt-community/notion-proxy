package com.saltpgp.notionproxy.developer.model;

public enum DeveloperStatus {
    ON_ASSIGNMENT("On Assignment"),
    PGP("PGP"),
    NONE("none");

    private final String status;

    DeveloperStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static boolean isValid(String filter) {
        for (DeveloperStatus value : values()) {
            if (value.getStatus().equalsIgnoreCase(filter)) {
                return true;
            }
        }
        return false;
    }
}
