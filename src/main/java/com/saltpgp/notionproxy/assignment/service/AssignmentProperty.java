package com.saltpgp.notionproxy.assignment.service;

class AssignmentProperty {
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
        public static final String NAME = "Name";
        public static final String SCORE = "Score";
        public static final String CATEGORIES = "Categories";
        public static final String COMMENT = "Comment";
    }

    public static final class Name {
        public static final String TITLE = "title";
    }

    public static final class Title {
        public static final String PLAIN_TEXT = "plain_text";
    }

    public static final class Score {
        public static final String NUMBER = "number";

    }

    public static final class Categories {
        public static final String MULTI_SELECT = "multi_select";
    }

    public static final class MultiSelect {
        public static final String NAME = "name";
    }
    public static final class Comment {
        public static final String RICH_TEXT = "rich_text";
    }

    public static final class RichText {
        public static final String PLAIN_TEXT = "plain_text";
    }
}
