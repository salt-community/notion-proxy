package com.saltpgp.notionproxy.modules.assignment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.saltpgp.notionproxy.modules.assignment.service.AssignmentProperty.*;

import java.util.ArrayList;
import java.util.List;

class AssignmentMapper {
    public final static String noCommentMessage = "No comment";


    public static int getScore(JsonNode properties) {
        return properties.get(Properties.SCORE).get(Score.NUMBER).asInt();
    }

    public static String getName(JsonNode properties) {
        return properties.get(Properties.NAME).get(Name.TITLE).get(0).get(Title.PLAIN_TEXT).asText();
    }

    public static List<String> getCategories(JsonNode properties) {
        List<String> categories = new ArrayList<>();
        if (properties.get(Properties.CATEGORIES) != null) {
            properties.get(Properties.CATEGORIES).get(Categories.MULTI_SELECT).forEach(category ->
                    categories.add(category.get(MultiSelect.NAME).asText()));
        }
        return categories;
    }

    public static String getScoreComment(JsonNode properties) {
        try {
            return properties.get(Properties.COMMENT).get(Comment.RICH_TEXT)
                    .get(0).get(RichText.PLAIN_TEXT).asText();
        } catch (Exception e) {
            return noCommentMessage;
        }
    }

}
