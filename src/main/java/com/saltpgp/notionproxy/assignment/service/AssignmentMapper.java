package com.saltpgp.notionproxy.assignment.service;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

class AssignmentMapper {
    public final static String noCommentMessage = "No comment";

    public static int getScore(JsonNode properties) {
        return properties.get("Score").get("number").asInt();
    }

    public static String getName(JsonNode properties) {
        return properties.get("Name").get("title").get(0).get("plain_text").asText();
    }

    public static List<String> getCategories(JsonNode properties) {
        List<String> categories = new ArrayList<>();
        if (properties.get("Categories") != null) {
            properties.get("Categories").get("multi_select").forEach(category ->
                    categories.add(category.get("name").asText()));
        }
        return categories;
    }

    public static String getScoreComment(JsonNode properties) {
        try {
            return properties.get("Comment").get("rich_text")
                    .get(0).get("plain_text").asText();
        } catch (Exception e) {
            return noCommentMessage;
        }
    }

}
