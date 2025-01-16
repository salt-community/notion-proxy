package com.saltpgp.notionproxy.developer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.saltpgp.notionproxy.developer.model.Developer;
import com.saltpgp.notionproxy.exceptions.NotionException;
import com.saltpgp.notionproxy.service.NotionApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.saltpgp.notionproxy.developer.service.DeveloperService.DeveloperServiceUtility.*;
import static com.saltpgp.notionproxy.service.NotionServiceFilters.filterBuilder;

@Service
@Slf4j
public class DeveloperService {

    private final NotionApiService notionApiService;
    private final String DATABASE_ID;
    private static final String FILTER = """
                        "filter": {
                            "property": "Status",
                            "select": {
                                "equals": "%s"
                            }
                        }
                    """;

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
            JsonNode response = notionApiService.fetchDatabase(DATABASE_ID, filterBuilder(nextCursor, filter, FILTER));

            response.get("results").elements().forEachRemaining(page -> {
                if (page.get("properties").get("Name").get("title").get(0) == null) return;
                allDevelopers.add(createDeveloperFromNotionPage(page));
            });

            nextCursor = response.get("next_cursor").asText();
            hasMore = response.get("has_more").asBoolean();
        }
        return allDevelopers;
    }

    public Developer getDeveloperById(UUID id) throws NotionException {
        JsonNode page = notionApiService.fetchPage(id.toString());
        return createDeveloperFromNotionPage(page);
    }

    private static Developer createDeveloperFromNotionPage(JsonNode page) {
        var properties = page.get("properties");
        var githubUrl = getDeveloperGithubUrl(properties);
        return new Developer(
                getDeveloperName(properties),
                UUID.fromString(getDeveloperId(page)),
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
            return properties.get("GitHub").get("url").asText().equals("null") ? NULL_STATUS
                    : properties.get("GitHub").get("url").asText();
        }

        public static String getDeveloperEmail(JsonNode properties) {
            return properties.get("Private Email").get("email").asText().equals("null") ? NULL_STATUS
                    : properties.get("Private Email").get("email").asText();
        }

        public static String getDeveloperGithubImageUrl(String githubUrl) {
            return githubUrl == null ? null : githubUrl + ".png";
        }

    }

}
