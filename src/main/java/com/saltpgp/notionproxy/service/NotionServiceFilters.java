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
}
