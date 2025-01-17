package com.saltpgp.notionproxy.staff;

public class StaffFilter {

    public static final String STAFF_FILTER = """
                        "filter": {
                        "property": "Guild",
                        "multi_select":     {
                            "contains": "%s"
                            }
                        }""";

    public static final String STAFF_FILTER_SINGLE = """
                        "filter": {
                        "property": "Person",
                        "multi_select":     {
                            "contains": "%s"
                            }
                        }""";
}
