package com.saltpgp.notionproxy.modules.staff;

public class StaffFilter {

    public static final String STAFF_FILTER = """
                        "filter": {
                        "property": "Guild",
                        "multi_select":     {
                            "contains": "%s"
                            }
                        }""";

    public static final String STAFF_FILTER_RESPONSIBLE = """
                        "filter": {
                        "property": "Responsible",
                        "people":     {
                            "contains": "%s"
                            }
                        }""";

    public static final String STAFF_FILTER_SINGLE = """
                        "filter": {
                        "property": "Person",
                        "people":     {
                            "contains": "%s"
                            }
                        }""";
}
