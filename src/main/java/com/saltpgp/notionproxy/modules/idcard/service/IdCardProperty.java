package com.saltpgp.notionproxy.modules.idcard.service;

class IdCardProperty {

    public static final String FILTER_EMAIL = """
                "filter": {
                    "property": "Email",
                    "email": {
                        "equals": "%s"
                    }
                }
            """;

    public static final class NotionObject {
        public static final String RESULTS = "results";
    }

    public static final class Results {
        public static final String PROPERTIES = "properties";
        public static final String ID = "id";
    }

    public static final class Properties {
        public static final String COURSE = "Course";
        public static final String NAME = "Name";
        public static final String GITHUB = "GitHub";
        public static final String EMAIL = "Email";
    }

    public static final class Course {
        public static final String SELECT = "select";
    }

    public static final class Select {
        public static final String NAME = "name";
    }
    public static final class Name {
        public static final String TITLE = "title";
    }

    public static final class Title {
        public static final String PLAIN_TEXT = "plain_text";
    }

    public static final class GitHub {
        public static final String URL = "url";
    }

    public static final class Email {
        public static final String EMAIL = "email";
    }
}