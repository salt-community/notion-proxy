package com.saltpgp.notionproxy.developer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.saltpgp.notionproxy.developer.model.Developer;
import com.saltpgp.notionproxy.exceptions.NotionException;
import com.saltpgp.notionproxy.service.NotionApiService;
import com.saltpgp.notionproxy.service.NotionServiceFilters;
import com.saltpgp.notionproxy.service.NotionServiceUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.saltpgp.notionproxy.developer.service.DeveloperService.DeveloperServiceUtility.*;
import static com.saltpgp.notionproxy.service.NotionServiceFilters.getFilterDeveloper;

@Service
@Slf4j
public class DeveloperService {

    private final NotionApiService notionApiService;
    private final String DATABASE_ID;

    public DeveloperService(NotionApiService notionApiService, @Value("${DATABASE_ID}") String DATABASE_ID) {
        this.notionApiService = notionApiService;
        this.DATABASE_ID = DATABASE_ID;
    }

    public List<Developer> getAllDevelopers(String filter) throws NotionException {
        List<Developer> allDevelopers = new ArrayList<>();
        String nextCursor = null;
        boolean hasMore = true;
        while (hasMore) {
            //TODO:använda nya filterBuilder
            JsonNode response = notionApiService.fetchDatabase(DATABASE_ID, getFilterDeveloper(nextCursor, filter));

            response.get("results").elements().forEachRemaining(element -> {
                if (element.get("properties").get("Name").get("title").get(0) == null) return;
                allDevelopers.add(createDeveloperFromResultArrayElement(element));
            });

            nextCursor = response.get("next_cursor").asText();
            hasMore = response.get("has_more").asBoolean();
        }
        return allDevelopers;
    }

    public Developer getDeveloperById(UUID id) throws NotionException {
        JsonNode response = notionApiService.fetchPage(id.toString());
        String name = response.get("properties").get("Name").get("title").get(0).get("plain_text").asText();
        String githubUrl = response.get("properties").get("GitHub").get("url").asText();
        String githubImage = githubUrl + "png";
        String email = response.get("properties").get("Private Email").get("email").asText();
        String status = NotionServiceUtility.getDeveloperStatus(response);
        String totalScore = NotionServiceUtility.getDeveloperTotalScore(response);
        return new Developer(name, id, githubUrl,
                githubImage, email, status, totalScore);

    }

    private static Developer createDeveloperFromResultArrayElement(JsonNode resultElement) {
        JsonNode properties = resultElement.get("properties");
        var githubUrl = getDeveloperGithubUrl(properties);
        return new Developer(
                getDeveloperName(properties),
                UUID.fromString(getDeveloperId(properties)),
                githubUrl,
                getDeveloperGithubImageUrl(githubUrl),
                getDeveloperEmail(properties),
                getDeveloperStatus(properties),
                getDeveloperTotalScore(properties));
    }

    public static class DeveloperServiceUtility {

        public final static String noCommentMessage = "No comment";
        public final static String NULL_STATUS = "none";

        public static String getScoreComment(JsonNode properties) {
            try {
                return properties.get("Comment").get("rich_text")
                        .get(0).get("plain_text").asText();
            } catch (Exception e) {
                return noCommentMessage;
            }
        }

        public static String getDeveloperStatus(JsonNode properties) {
            try {
                return properties.get("Status").get("select")
                        .get("name").asText();
            } catch (Exception e) {
                return NULL_STATUS;
            }
        }

        public static String getDeveloperTotalScore(JsonNode properties) {
            try {
                return String.valueOf(properties.get("Total Score").get("formula")
                        .get("number").asInt());
            } catch (Exception e) {
                return NULL_STATUS;
            }
        }

        public static String getDeveloperId(JsonNode element) {
            return element.get("id").asText();
        }

        public static String getDeveloperName(JsonNode properties) {
            return properties.get("Name").get("title").get(0).get("plain_text").asText();
        }

        public static String getDeveloperGithubUrl(JsonNode properties) {
            return properties.get("GitHub").get("url").asText().equals("null") ? null
                    : properties.get("GitHub").get("url").asText();
        }

        public static String getDeveloperEmail(JsonNode properties) {
            return properties.get("Private Email").get("email").asText().equals("null") ? null
                    : properties.get("Private Email").get("email").asText();
        }

        public static String getDeveloperGithubImageUrl(String githubUrl) {
            return githubUrl == null ? null : githubUrl + ".png";
        }

    }

}
