package com.saltpgp.notionproxy.service;

import com.fasterxml.jackson.databind.JsonNode;

public class NotionServiceUtility {

    public final static String noCommentMessage = "No comment";
    public final static String NULL_STATUS = "none";

    public static String getScoreComment(JsonNode element) {
        try {
            return element.get("properties").get("Comment").get("rich_text")
                    .get(0).get("plain_text").asText();
        } catch (Exception e) {
            return noCommentMessage;
        }
    }

    public static String getDeveloperStatus(JsonNode element) {
        try {
            return element.get("properties").get("Status").get("select")
                    .get("name").asText();
        } catch (Exception e) {
            return NULL_STATUS;
        }
    }

    public static String getDeveloperTotalScore(JsonNode element) {
        try {
            return String.valueOf(element.get("properties").get("Total Score").get("formula")
                    .get("number").asInt());
        } catch (Exception e) {
            return NULL_STATUS;
        }
    }

}
