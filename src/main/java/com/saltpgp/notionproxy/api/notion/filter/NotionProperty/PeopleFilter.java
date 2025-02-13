package com.saltpgp.notionproxy.api.notion.filter.NotionProperty;

public enum PeopleFilter {
    CONTAINS ("contains"),
    DOES_NOT_CONTAIN ("does_not_contain"),
    IS_EMPTY ("is_empty"),
    IS_NOT_EMPTY ("is_not_empty");

    private final String value;

    private PeopleFilter(String s) {
        value = s;
    }

    public String toString() {
        return this.value;
    }
}
