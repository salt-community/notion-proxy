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

    private List<Assignment> fetchAllAssignments(UUID developerId) throws NotionException {
        List<Assignment> assignments = new ArrayList<>();
        String nextCursor = null;
        boolean hasMore = true;

        while (hasMore) {
            JsonNode response = notionApiService.fetchDatabase(
                    SCORE_DATABASE_ID, filterBuilder(nextCursor, String.valueOf(developerId), FILTER));

            if (response != null) {
                assignments.addAll(extractAssignments(response));
            }

            nextCursor = response.get("next_cursor").asText();
            hasMore = response.get("has_more").asBoolean();
        }

        return assignments;
    }

    public List<Assignment> getAssignmentsFromDeveloper(UUID developerId) throws NotionException {
        return fetchAllAssignments(developerId);
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

            foundAssignment = extractAssignmentById(response, assignmentId);
            if (foundAssignment != null) {
                break;
            }

            nextCursor = response.get("next_cursor").asText();
            hasMore = response.get("has_more").asBoolean();
        }

        return foundAssignment;
    }



    private Assignment extractAssignmentById(JsonNode response, UUID assignmentId) {
        for (JsonNode element : response.get("results")) {
            if (element.has("id") && assignmentId.toString().equals(element.get("id").asText())) {
                return extractScore(element);
            }
        }
        return null;
    }

    private List<Assignment> extractAssignments(JsonNode response) {
        List<Assignment> assignments = new ArrayList<>();
        response.get("results").elements().forEachRemaining(element -> {
            Assignment assignment = extractScore(element);
            if (assignment != null) {
                assignments.add(assignment);
            }
        });
        return assignments;
    }

    private Assignment extractScore(JsonNode element) {
        if (element.get("properties").get("Score") == null) {
            return null;
        }

        List<String> categories = new ArrayList<>();
        if (element.get("properties").get("Categories") != null) {
            element.get("properties").get("Categories").get("multi_select").forEach(category ->
                    categories.add(category.get("name").asText()));
        }

        return new Assignment(
                element.get("properties").get("Name").get("title").get(0).get("plain_text").asText(),
                element.get("properties").get("Score").get("number").asInt(),
                categories,
                getScoreComment(element)
        );
    }

    public static String getScoreComment(JsonNode element) {
        try {
            return element.get("properties").get("Comment").get("rich_text")
                    .get(0).get("plain_text").asText();
        } catch (Exception e) {
            return noCommentMessage;
        }
    }
}
