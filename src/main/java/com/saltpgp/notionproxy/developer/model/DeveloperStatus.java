package com.saltpgp.notionproxy.developer.model;

public enum DeveloperStatus {
    PRE_COURSE("Precourse"),
    TALENT_POOL("Talent Pool"),
    LET_GO("LetGo"),
    ON_ASSIGNMENT("On Assignment"),
    DONE("Done"),
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
            if (value.getStatus().equals(filter)) {
                return true;
            }
        }
        return false;
    }
}
