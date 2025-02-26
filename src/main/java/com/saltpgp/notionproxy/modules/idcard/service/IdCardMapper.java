package com.saltpgp.notionproxy.modules.idcard.service;

import com.fasterxml.jackson.databind.JsonNode;

class IdCardMapper {
    public static String getGitHub(JsonNode properties) {
        return properties.get("GitHub").get("url").asText();
    }

    public static String getEmail(JsonNode properties) {
        return properties.get("Email").get("email").asText();
    }

    public static String getCourse(JsonNode properties) {
        return properties.get("Course").get("select").get("name").asText();
    }

    public static String getText(JsonNode properties) {
        return properties.get("Name").get("title").get(0).get("text").get("content").asText();
    }

    public static String getId(JsonNode page) {
        return page.get("id").asText();
    }
}
