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
                }""";

    public static final String FILTER_ON_ASSIGNMENT = """
                {
                    "filter": {
                        "property": "Status",
                        "select": {
                            "equals": "On Assignment"
                        }
                    }
                }""";

    public static String getFilterOnAssignment (String cursor) {
        return cursor == null ? FILTER_ON_ASSIGNMENT :
                String.format("""
                        {
                            "start_cursor": "%s"
                            "filter": {
                                "property": "Status",
                                "select": {
                                    "equals": "On Assignment"
                                }
                            }
                        }""", cursor);
    }

    public static String getFilterDeveloper (String cursor, String filter) {
        String developerFilter = "{\n";
        if (cursor != null) {
            developerFilter += String.format("""
                        "start_cursor": "%s"
                    """, cursor);
        }
        if (filter != null && !filter.equals("none")) {
            developerFilter += String.format("""
                        "filter": {
                            "property": "Status",
                            "select": {
                                "equals": "%s"
                            }
                        }
                    """, filter);
        }
        return developerFilter += "}";
    }

    public static String filterBuilder(String cursor, String filterParam, String filterString) {
        return """
                {
                """ + buildCursor(cursor) + buildFilter(filterString, filterParam) + """
                
                }
                """;
    }

    public static String filterBuilder(String cursor) {
        return """
                {
                """ + buildCursor(cursor) +"""
                }
                """;
    }

    private static String buildFilter(String filter, String filterParam) {
        if(filter == null || filterParam == null || filterParam.equals("none")) {
            return "";
        }
        return String.format(filter, filterParam);
    }

    private static String buildCursor(String cursor) {
        if (cursor == null) {
            return "";
        }
        return String.format("""
                "start_cursor": "%s
                """, cursor);
    }
}
