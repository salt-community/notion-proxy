package com.saltpgp.notionproxy.service.NotionProperty;

public class NotionPropertyFilter {

    protected String filter, value, propertyType, propertyName;

    public String getFilter() {
        if (value == null || value.equals("none")) {
            return "";
        }
        return String.format("""
                "filter": {
                "property": "%s",
                "%s":     {
                          "%s": "%s"
                          }
                }
                """, propertyName, propertyType, filter, value);
    }

    public static NotionPropertyFilter SelectFilter(SelectFilter selectFilter, String value, String propertyName) {
        return new NotionPropertyFilter(selectFilter.toString(), value, "select", propertyName);
    }

    public NotionPropertyFilter(String filter, String value, String propertyType, String propertyName) {
        this.filter = filter;
        this.value = value;
        this.propertyType = propertyType;
        this.propertyName = propertyName;
    }
}
