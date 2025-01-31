package com.saltpgp.notionproxy.api.notion.filter.NotionProperty;

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

    public static NotionPropertyFilter selectFilter(SelectFilter selectFilter, String value, String propertyName) {
        return new NotionPropertyFilter(selectFilter.toString(), value, "select", propertyName);
    }

    public static NotionPropertyFilter multiSelectFilter(MultiSelectFilter filterType, String value, String propertyName) {
        return new NotionPropertyFilter(filterType.toString(), value, "multi_select", propertyName);
    }

    public static NotionPropertyFilter peopleFilter(PeopleFilter filterType, String value, String propertyName) {
        return new NotionPropertyFilter(filterType.toString(), value, "people", propertyName);
    }

    public NotionPropertyFilter(String filter, String value, String propertyType, String propertyName) {
        this.filter = filter;
        this.value = value;
        this.propertyType = propertyType;
        this.propertyName = propertyName;
    }
}
