package com.saltpgp.notionproxy.service.NotionProperty;

public enum MultiSelectFilter {
    CONTAINS ("contains"),
    DOES_NOT_CONTAIN ("does_not_contain"),
    IS_EMPTY ("is_empty"),
    IS_NOT_EMPTY ("is_not_empty");

    private final String value;

    private MultiSelectFilter(String s) {
        value = s;
    }

    public String toString() {
        return this.value;
    }
}
