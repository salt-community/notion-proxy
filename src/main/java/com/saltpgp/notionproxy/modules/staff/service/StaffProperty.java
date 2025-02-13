package com.saltpgp.notionproxy.modules.staff.service;

public class StaffProperty {

    public final static String CACHE_ID = "staff_";
    public final static String CACHE_ID_CONSULTANTS = "staff_consultants_";
    public static final String NULL = "Null";

    public static final class StaffFilter {

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

    public static final class NotionObject {
        public static final String RESULTS = "results";
        public static final String NEXT_CURSOR = "next_cursor";
        public static final String HAS_MORE = "has_more";
    }

    public static final class Results {
        public static final String PROPERTIES = "properties";
        public static final String ID = "id";
    }

    public static final class Properties {
        public static final String PERSON = "Person";
        public static final String GUILD = "Guild";
        public static final String EMAIL = "Email";
        public static final String NAME = "Name";
    }

    public static final class Person {
        public static final String PEOPLE = "people";
        public static final String EMAIL = "email";
        public static final String NAME = "name";
    }

    public static final class People {
        public static final String ID = "id";
        public static final String PERSON = "person";
    }

    public static final class Guild {
        public static final String MULTI_SELECT = "multi_select";
    }

    public static final class MultiSelect {
        public static final String NAME = "name";
    }

    public static final class Email {
        public static final String EMAIL = "email";

    }

    public static final class Name {
        public static final String TITLE = "title";
    }

    public static final class Title {
        public static final String PLAIN_TEXT = "plain_text";
    }
}
