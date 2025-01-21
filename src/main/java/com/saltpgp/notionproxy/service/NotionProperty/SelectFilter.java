package com.saltpgp.notionproxy.service.NotionProperty;

public enum SelectFilter {
    EQUALS ("equals");

    private final String value;

    private SelectFilter(String s) {
        value = s;
    }

    public String toString() {
        return this.value;
    }
}
