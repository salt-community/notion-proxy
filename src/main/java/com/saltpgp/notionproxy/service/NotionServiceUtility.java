package com.saltpgp.notionproxy.service;

import com.fasterxml.jackson.databind.JsonNode;

public class NotionServiceUtility {

    public final static String noCommentMessage = "No comment";

    public static String GetScoreComment(JsonNode element) {
        JsonNode text = element.get("properties").get("Comment").get("rich_text");
        if(text.get(0) != null) {
            return text.get(0).get("plain_text").asText();
        }
        return noCommentMessage;
    }

}
