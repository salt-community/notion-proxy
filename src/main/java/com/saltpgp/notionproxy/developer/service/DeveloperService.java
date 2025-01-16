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

@Service
@Slf4j
public class DeveloperService {

    private final NotionApiService notionApiService;
    private final String DATABASE_ID;
    private final String SCORE_DATABASE_ID;
    private final String CORE_DATABASE_ID;

    public DeveloperService(NotionApiService notionApiService,
                            @Value("${DATABASE_ID}") String DATABASE_ID,
                            @Value("${SCORE_DATABASE_ID}") String SCORE_DATABASE_ID,
                            @Value("${CORE_DATABASE_ID}") String CORE_DATABASE_ID) {
        this.notionApiService = notionApiService;
        this.DATABASE_ID = DATABASE_ID;
        this.SCORE_DATABASE_ID = SCORE_DATABASE_ID;
        this.CORE_DATABASE_ID = CORE_DATABASE_ID;
    }

    public List<Developer> getAllDevelopers(String filter) throws NotionException {
        List<Developer> allDevelopers = new ArrayList<>();
        String nextCursor = null;
        boolean hasMore = true;
        while (hasMore) {
            JsonNode response = notionApiService.fetchDatabase(DATABASE_ID, NotionServiceFilters.getFilterDeveloper(nextCursor, filter));

            response.get("results").elements().forEachRemaining(element -> {
                JsonNode properties = element.get("properties");
                if (properties.get("Name").get("title").get(0) == null) return;
                String githubUrl = element.get("properties").get("GitHub").get("url").asText().equals("null") ? null
                        : properties.get("GitHub").get("url").asText();

                String githubImageUrl = githubUrl == null ? null : githubUrl + ".png";
                String status = NotionServiceUtility.getDeveloperStatus(element);
                String totalScore = NotionServiceUtility.getDeveloperTotalScore(element);
                String email = properties.get("Private Email").get("email").asText().equals("null") ? null
                        : properties.get("Private Email").get("email").asText();

                var developer = new Developer(
                        properties.get("Name").get("title").get(0).get("plain_text").asText(),
                        UUID.fromString(element.get("id").asText()),
                        githubUrl,
                        githubImageUrl,
                        email,
                        status,
                        totalScore);

                allDevelopers.add(developer);
            });

            nextCursor = response.get("next_cursor").asText();
            hasMore = response.get("has_more").asBoolean();
        }
        return allDevelopers;
    }

    public static class DeveloperServiceUtility {

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

}
