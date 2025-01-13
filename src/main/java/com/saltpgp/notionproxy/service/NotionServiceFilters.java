package com.saltpgp.notionproxy.service;

public class NotionServiceFilters {

    public static final String FILTER_RESPONSIBLE_PEOPLE = """
                {
                    "filter": {
                        "property": "Guild",
                        "multi_select": {
                            "contains": "P&T"
                        }
                    }
                }
                """;

    public static final String FILTER_ON_ASSIGNMENT = """
                {
                    "filter": {
                        "property": "Status",
                        "select": {
                            "equals": "On Assignment"
                        }
                    }
                }
                """;

    public static String getFilterOnAssignment (String cursor)  {
        if(cursor == null) {
            return FILTER_ON_ASSIGNMENT;
        }
            return String.format("""
                {
                    "start_cursor": %s
                    "filter": {
                        "property": "Status",
                        "select": {
                            "equals": "On Assignment"
                        }
                    }
                }
                """,cursor);
    }

    public static String getFilterDeveloper (String cursor, String filter) {
        if (filter == null || filter.equals("none")) {
            if (cursor == null) {
                return "{" +
                        "}";
            }
            return String.format("""
                {
                    "start_cursor": %s
                    }
                }
                """,cursor);
        }
        if (cursor == null) {
            return String.format("""
                {
                    "filter": {
                        "property": "Status",
                        "select": {
                            "equals": %s
                        }
                    }
                }
                """, filter);
        }
        return String.format("""
                {
                    "start_cursor": %s
                    "filter": {
                        "property": "Status",
                        "select": {
                            "equals": %s
                        }
                    }
                }
                """, cursor, filter);
    }
}
