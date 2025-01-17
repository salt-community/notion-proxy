package com.saltpgp.notionproxy.developer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.saltpgp.notionproxy.developer.model.Responsible;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class DeveloperServiceUtility {

    private static final String STATUS_NONE = "none";

    private static final class Properties {
        private static final String STATUS = "Status";
        private static final String TOTAL_SCORE = "Total Score";
        private static final String ID = "id";
        private static final String NAME = "Name";
        private static final String GITHUB = "GitHub";
        private static final String PRIVATE_EMAIL = "Private Email";
        private static final String RESPONSIBLE = "Responsible";
    }

    private static final class Status {
        private static final String SELECT = "select";
        private static final String NAME_KEY = "name";
    }

    private static final class TotalScore {
        private static final String FORMULA = "formula";
        private static final String NUMBER = "number";
    }

    private static final class Name {
        private static final String TITLE = "title";
        private static final String PLAIN_TEXT = "plain_text";
    }

    private static final class GitHub {
        private static final String URL = "url";
    }

    private static final class PrivateEmail {
        private static final String EMAIL = "email";
    }

    private static final class NotionResponsible {
        private static final String PEOPLE = "people";
        private static final String PERSON = "person";
        private static final String EMAIL = "email";
    }

    private static final String NULL = "null";
    private static final String PNG = ".png";

    public static String getDeveloperStatus(JsonNode properties) {
        try {
            return properties.get(Properties.STATUS).get(Status.SELECT)
                    .get(Status.NAME_KEY).asText();
        } catch (Exception e) {
            return STATUS_NONE;
        }
    }

    public static String getDeveloperTotalScore(JsonNode properties) {
        try {
            return String.valueOf(properties.get(Properties.TOTAL_SCORE).get(TotalScore.FORMULA)
                    .get(TotalScore.NUMBER).asInt());
        } catch (Exception e) {
            return STATUS_NONE;
        }
    }

    public static String getDeveloperId(JsonNode element) {
        return element.get(Properties.ID).asText();
    }

    public static String getDeveloperName(JsonNode properties) {
        return properties.get(Properties.NAME).get(Name.TITLE).get(0).get(Name.PLAIN_TEXT).asText();
    }

    public static String getDeveloperGithubUrl(JsonNode properties) {
        return properties.get(Properties.GITHUB).get(GitHub.URL).asText().equals(NULL) ? STATUS_NONE
                : properties.get(Properties.GITHUB).get(GitHub.URL).asText();
    }

    public static String getDeveloperEmail(JsonNode properties) {
        return properties.get(Properties.PRIVATE_EMAIL).get(PrivateEmail.EMAIL).asText().equals(NULL) ? STATUS_NONE
                : properties.get(Properties.PRIVATE_EMAIL).get(PrivateEmail.EMAIL).asText();
    }

    public static String getDeveloperGithubImageUrl(String githubUrl) {
        return githubUrl == null ? null : githubUrl + PNG;
    }

    public static List<Responsible> getResponsibleList(JsonNode properties) {
        List<Responsible> responsibleList = new ArrayList<>();
        properties.get(Properties.RESPONSIBLE).get(NotionResponsible.PEOPLE).elements().forEachRemaining(responsible -> {
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
        return responsible.get(NotionResponsible.PERSON).get(NotionResponsible.EMAIL).asText();
    }

    private static UUID getResponsibleId(JsonNode responsible) throws Exception {
        return UUID.fromString(responsible.get(Properties.ID).asText());
    }

    private static String getResponsibleName(JsonNode responsible) throws Exception {
        return responsible.get(Status.NAME_KEY).asText();
    }
}
