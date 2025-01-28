package com.saltpgp.notionproxy.api.notion.filter.NotionProperty;

public enum SelectFilter {
    EQUALS ("equals"),
    DOES_NOT_EQUALS ("does_not_equal"),
    IS_EMPTY ("is_empty"),
    IS_NOT_EMPTY ("is_not_empty");

    private final String value;

    private SelectFilter(String s) {
        value = s;
    }

    public String toString() {
        return this.value;
    }
}
