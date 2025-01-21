package com.saltpgp.notionproxy.assignment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.saltpgp.notionproxy.assignment.model.Assignment;
import com.saltpgp.notionproxy.exceptions.NotionException;
import com.saltpgp.notionproxy.notionapi.NotionApiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.saltpgp.notionproxy.service.NotionServiceFilters.filterBuilder;

@Service
public class AssignmentService {

    public final static String noCommentMessage = "No comment";
    public static final String FILTER = """
            "filter": {
                "property": "💽 Developer",
                "relation": {
                    "contains": "%s"
                }
            }
            """;
    private final NotionApiService notionApiService;

    @Value("${SCORE_DATABASE_ID}")
    private String SCORE_DATABASE_ID;

    public AssignmentService(NotionApiService notionApiService) {
        this.notionApiService = notionApiService;
    }


    public Assignment getAssignmentFromDeveloper(UUID developerId, UUID assignmentId) throws NotionException {
        return fetchAssignmentById(developerId, assignmentId);
    }

    public List<Assignment> getAssignmentsFromDeveloper(UUID developerId) throws NotionException {
        List<Assignment> assignments = new ArrayList<>();
        String nextCursor = null;
        boolean hasMore = true;

        while (hasMore) {
            JsonNode response = notionApiService.fetchDatabase(
                    SCORE_DATABASE_ID, filterBuilder(nextCursor, developerId.toString(), FILTER));

            response.get("results").elements().forEachRemaining(elements -> {
                assignments.add(extractAssignments(elements));
            });

            nextCursor = response.get("next_cursor").asText();
            hasMore = response.get("has_more").asBoolean();
        }
        return assignments;
    }

    private Assignment extractAssignments(JsonNode elements) {
        var properties = elements.get("properties");
        return new Assignment(
                getName(properties),
                getScore(properties),
                getCategories(properties),
                getScoreComment(properties));
    }

    private Assignment fetchAssignmentById(UUID developerId, UUID assignmentId) throws NotionException {
        String nextCursor = null;
        boolean hasMore = true;
        Assignment foundAssignment = null;
        while (hasMore) {
            JsonNode response = notionApiService.fetchDatabase(
                    SCORE_DATABASE_ID, filterBuilder(nextCursor, String.valueOf(developerId), FILTER));
            if (response == null) {
                break;
            }

            //foundAssignment = extractAssignmentById(response, assignmentId);
            if (foundAssignment != null) {
                break;
            }

            nextCursor = response.get("next_cursor").asText();
            hasMore = response.get("has_more").asBoolean();
        }

        return foundAssignment;
    }
/*
    private Assignment extractAssignmentById(JsonNode response, UUID assignmentId) {
        for (JsonNode element : response.get("results")) {
            if (element.has("id") && assignmentId.toString().equals(element.get("id").asText())) {
                return extractScore(element);
            }
        }
        return null;
    }
*/

    private static int getScore(JsonNode properties) {
        return properties.get("Score").get("number").asInt();
    }

    private static String getName(JsonNode properties) {
        return properties.get("Name").get("title").get(0).get("plain_text").asText();
    }

    private static List<String> getCategories(JsonNode properties) {
        List<String> categories = new ArrayList<>();
        if (properties.get("Categories") != null) {
            properties.get("Categories").get("multi_select").forEach(category ->
                    categories.add(category.get("name").asText()));
        }
        return categories;
    }

    public static String getScoreComment(JsonNode properties) {
        try {
            return properties.get("Comment").get("rich_text")
                    .get(0).get("plain_text").asText();
        } catch (Exception e) {
            return noCommentMessage;
        }
    }

}
