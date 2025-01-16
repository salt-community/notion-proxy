package com.saltpgp.notionproxy.developer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.saltpgp.notionproxy.developer.model.Responsible;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class DeveloperServiceUtility {
    private final static String NULL_STATUS = "none";
    private static final String PROPERTY_STATUS = "Status";
    private static final String PROPERTY_TOTAL_SCORE = "Total Score";
    private static final String PROPERTY_ID = "id";
    private static final String PROPERTY_NAME = "Name";
    private static final String PROPERTY_TITLE = "title";
    private static final String PROPERTY_PLAIN_TEXT = "plain_text";
    private static final String PROPERTY_GITHUB = "GitHub";
    private static final String PROPERTY_URL = "url";
    private static final String PROPERTY_PRIVATE_EMAIL = "Private Email";
    private static final String PROPERTY_EMAIL = "email";
    private static final String PROPERTY_SELECT = "select";
    private static final String PROPERTY_NAME_KEY = "name";
    private static final String PROPERTY_FORMULA = "formula";
    private static final String PROPERTY_NUMBER = "number";
    private static final String PROPERTY_PERSON = "person";


    public static String getDeveloperStatus(JsonNode properties) {
        try {
            return properties.get(PROPERTY_STATUS).get(PROPERTY_SELECT)
                    .get(PROPERTY_NAME_KEY).asText();
        } catch (Exception e) {
            return NULL_STATUS;
        }
    }

    public static String getDeveloperTotalScore(JsonNode properties) {
        try {
            return String.valueOf(properties.get(PROPERTY_TOTAL_SCORE).get(PROPERTY_FORMULA)
                    .get(PROPERTY_NUMBER).asInt());
        } catch (Exception e) {
            return NULL_STATUS;
        }
    }

    public static String getDeveloperId(JsonNode element) {
        return element.get(PROPERTY_ID).asText();
    }

    public static String getDeveloperName(JsonNode properties) {
        return properties.get(PROPERTY_NAME).get(PROPERTY_TITLE).get(0).get(PROPERTY_PLAIN_TEXT).asText();
    }

    public static String getDeveloperGithubUrl(JsonNode properties) {
        return properties.get(PROPERTY_GITHUB).get(PROPERTY_URL).asText().equals("null") ? NULL_STATUS
                : properties.get(PROPERTY_GITHUB).get(PROPERTY_URL).asText();
    }

    public static String getDeveloperEmail(JsonNode properties) {
        return properties.get(PROPERTY_PRIVATE_EMAIL).get(PROPERTY_EMAIL).asText().equals("null") ? NULL_STATUS
                : properties.get(PROPERTY_PRIVATE_EMAIL).get(PROPERTY_EMAIL).asText();
    }

    public static String getDeveloperGithubImageUrl(String githubUrl) {
        return githubUrl == null ? null : githubUrl + ".png";
    }

    public static List<Responsible> getResponsibleList(JsonNode properties) {
        List<Responsible> responsibleList = new ArrayList<>();
        properties.get("Responsible").get("people").elements().forEachRemaining(responsible -> {
            try {
                responsibleList.add(new Responsible(
                        getResponsibleName(responsible),
                        getResponsibleId(responsible),
                        getResponsibleEmail(responsible)));
            } catch (Exception ignored) {}
        });
        return responsibleList;
    }

    private static String getResponsibleEmail(JsonNode responsible) throws Exception {
        return responsible.get(PROPERTY_PERSON).get(PROPERTY_EMAIL).asText();
    }

    private static UUID getResponsibleId(JsonNode responsible) throws Exception {
            return UUID.fromString(responsible.get(PROPERTY_ID).asText());
    }

    private static String getResponsibleName(JsonNode responsible) throws Exception {
        return responsible.get(PROPERTY_NAME_KEY).asText();
    }
}
