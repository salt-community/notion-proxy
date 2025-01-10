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
}
