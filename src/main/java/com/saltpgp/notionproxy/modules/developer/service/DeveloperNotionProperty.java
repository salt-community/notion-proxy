package com.saltpgp.notionproxy.modules.developer.service;

class DeveloperNotionProperty {

    public static final class NotionObject{
        public static final String RESULTS = "results";
        public static final String NEXT_CURSOR = "next_cursor";
        public static final String HAS_MORE = "has_more";
    }

    public static final class Results{
        public static final String PROPERTIES = "properties";
        public static final String ID = "id";
    }

    public static final class Properties {
        public static final String STATUS = "Status";
        public static final String TOTAL_SCORE = "Total Score";
        public static final String NAME = "Name";
        public static final String GITHUB = "GitHub";
        public static final String PRIVATE_EMAIL = "Private Email";
        public static final String RESPONSIBLE = "Responsible";
    }

    public static final class Status {
        public static final String SELECT = "select";
    }

    public static final class Select {
        public static final String NAME_KEY = "name";
    }

    public static final class TotalScore {
        public static final String FORMULA = "formula";
    }
    public static final class Formula {
        public static final String NUMBER = "number";
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

    public static final class PrivateEmail {
        public static final String EMAIL = "email";
    }

    public static final class NotionResponsible {
        public static final String PEOPLE = "people";
        public static final String PERSON = "person";
        public static final String EMAIL = "email";
    }
    
}
