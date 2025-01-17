package com.saltpgp.notionproxy.developer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.saltpgp.notionproxy.developer.model.Responsible;
import static com.saltpgp.notionproxy.developer.service.DeveloperNotionProperty.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class DeveloperServiceUtility {

    private static final String STATUS_NONE = "none";

    private static final String NULL = "null";
    private static final String PNG = ".png";

    public static String getDeveloperStatus(JsonNode properties) {
        try {
            return properties.get(Properties.STATUS).get(Status.SELECT).get(Select.NAME_KEY).asText();
        } catch (Exception e) {
            return STATUS_NONE;
        }
    }

    public static String getDeveloperTotalScore(JsonNode properties) {
        try {
            return String.valueOf(properties.get(Properties.TOTAL_SCORE).get(TotalScore.FORMULA)
                    .get(Formula.NUMBER).asInt());
        } catch (Exception e) {
            return STATUS_NONE;
        }
    }

    public static String getDeveloperId(JsonNode element) {
        return element.get(Properties.ID).asText();
    }

    public static String getDeveloperName(JsonNode properties) {
        return properties.get(Properties.NAME).get(Name.TITLE).get(0).get(Title.PLAIN_TEXT).asText();
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
        return responsible.get(Select.NAME_KEY).asText();
    }
}
