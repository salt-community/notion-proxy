package com.saltpgp.notionproxy.developer.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

import static com.saltpgp.notionproxy.developer.service.DeveloperNotionProperty.*;

import java.util.UUID;
import java.util.function.Supplier;

@Slf4j
class DeveloperNotionMapper {

    private static final String STATUS_NONE = "none";

    private static final String NULL = "null";
    private static final String PNG = ".png";

    public static String getDeveloperStatus(JsonNode properties) {
        log.debug("Attempting to retrieve developer status.");
        return tryToGet(() -> properties.get(Properties.STATUS).get(Status.SELECT).get(Select.NAME_KEY).asText());
    }

    public static String getDeveloperTotalScore(JsonNode properties) {
        log.debug("Attempting to retrieve developer total score.");
        return tryToGet(() -> String.valueOf(properties.get(Properties.TOTAL_SCORE).get(TotalScore.FORMULA).get(Formula.NUMBER).asInt()));
    }

    public static String getDeveloperId(JsonNode element) {
        log.debug("Attempting to retrieve developer ID.");
        return element.get(Results.ID).asText();
    }

    public static String getDeveloperName(JsonNode properties) {
        log.debug("Attempting to retrieve developer name.");
        return properties.get(Properties.NAME).get(Name.TITLE).get(0).get(Title.PLAIN_TEXT).asText();
    }

    public static String getDeveloperGithubUrl(JsonNode properties) {
        log.debug("Attempting to retrieve developer GitHub URL.");
        return checkIfNull(() -> properties.get(Properties.GITHUB).get(GitHub.URL).asText());
    }

    public static String getDeveloperEmail(JsonNode properties) {
        log.debug("Attempting to retrieve developer email.");
        return checkIfNull(() -> properties.get(Properties.PRIVATE_EMAIL).get(PrivateEmail.EMAIL).asText());
    }

    public static String getDeveloperGithubImageUrl(String githubUrl) {
        log.debug("Generating GitHub image URL for: {}", githubUrl);
        return githubUrl.equals(STATUS_NONE) ? STATUS_NONE : githubUrl + PNG;
    }

    public static String getResponsibleEmail(JsonNode responsible) {
        log.debug("Attempting to retrieve responsible person's email.");
        return responsible.get(NotionResponsible.PERSON).get(NotionResponsible.EMAIL).asText();
    }

    public static UUID getResponsibleId(JsonNode responsible) {
        log.debug("Attempting to retrieve responsible person's ID.");
        return UUID.fromString(responsible.get(Results.ID).asText());
    }

    public static String getResponsibleName(JsonNode responsible) {
        log.debug("Attempting to retrieve responsible person's name.");
        return responsible.get(Select.NAME_KEY).asText();
    }

    private static String checkIfNull(Supplier<String> supplier){
        return supplier.get().equals(NULL) ? STATUS_NONE : supplier.get();
    }

    private static String tryToGet(Supplier<String> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            return STATUS_NONE;
        }
    }
}
