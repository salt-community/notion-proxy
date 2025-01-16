package com.saltpgp.notionproxy.staff;

public class StaffFilter {

    public static final String STAFF_FILTER = """
                        "filter": {
                        "property": "Guild",
                        "multi_select":     {
                            "contains": "%s"
                            }
                        }""";

    public static String filterBuilder(String filter, String filterType, String cursor) {
        return """
                {
                """ + buildCursor(cursor) + buildFilter(filterType, filter) + """
                
                }
                """;
    }

    private static String buildFilter(String filter, String filterParam) {
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
