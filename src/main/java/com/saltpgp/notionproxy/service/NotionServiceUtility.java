package com.saltpgp.notionproxy.service;

import com.fasterxml.jackson.databind.JsonNode;

public class NotionServiceUtility {

    public final static String noCommentMessage = "No comment";

    public static String getScoreComment(JsonNode element) {
        try {
            return element.get("properties").get("Comment").get("rich_text")
                    .get(0).get("plain_text").asText();
        } catch (Exception e) {
            return noCommentMessage;
        }
    }

}
