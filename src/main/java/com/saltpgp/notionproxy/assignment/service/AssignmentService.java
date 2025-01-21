package com.saltpgp.notionproxy.assignment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.saltpgp.notionproxy.assignment.model.Assignment;
import com.saltpgp.notionproxy.exceptions.NotionException;
import com.saltpgp.notionproxy.service.NotionApiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AssignmentService {

    public final static String noCommentMessage = "No comment";
    private final NotionApiService notionApiService;

    @Value("${SCORE_DATABASE_ID}")
    private String SCORE_DATABASE_ID;

    public AssignmentService(NotionApiService notionApiService) {
        this.notionApiService = notionApiService;
    }

    public Assignment getAssignmentFromDeveloper(UUID developerId, UUID assignmentId) throws NotionException {
        ObjectNode queryBody = getDeveloperNode(developerId);
        return fetchAssignmentById(queryBody, assignmentId);
    }

    public List<Assignment> getAssignmentsFromDeveloper(UUID developerId) throws NotionException {
        ObjectNode queryBody = getDeveloperNode(developerId);
        return fetchAllAssignments(queryBody);
    }

    private Assignment fetchAssignmentById(ObjectNode queryBody, UUID assignmentId) throws NotionException {
        String nextCursor = null;
        boolean hasMore = true;
        Assignment foundAssignment = null;

        while (hasMore) {
            if (nextCursor != null) {
                queryBody.put("start_cursor", nextCursor);
            }

            JsonNode response = notionApiService.fetchDatabase(SCORE_DATABASE_ID, queryBody);
            if (response == null) {
                break;
            }

            foundAssignment = extractAssignmentById(response, assignmentId);
            if (foundAssignment != null) {
                break;
            }

            nextCursor = getNextCursor(response);
            hasMore = hasMorePages(response);
        }

        return foundAssignment;
    }

    private List<Assignment> fetchAllAssignments(ObjectNode queryBody) throws NotionException {
        List<Assignment> assignments = new ArrayList<>();
        String nextCursor = null;
        boolean hasMore = true;

        while (hasMore) {
            if (nextCursor != null) {
                queryBody.put("start_cursor", nextCursor);
            }

            JsonNode response = notionApiService.fetchDatabase(SCORE_DATABASE_ID, queryBody);
            if (response != null) {
                assignments.addAll(extractAssignments(response));
            }

            nextCursor = getNextCursor(response);
            hasMore = hasMorePages(response);
        }

        return assignments;
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

    private String getNextCursor(JsonNode response) {
        return response.has("next_cursor") && !response.get("next_cursor").isNull()
                ? response.get("next_cursor").asText()
                : null;
    }

    private boolean hasMorePages(JsonNode response) {
        return response.has("has_more") && response.get("has_more").asBoolean();
    }

    private ObjectNode getDeveloperNode(UUID id) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode relationNode = objectMapper.createObjectNode();
        relationNode.put("contains", String.valueOf(id));
        ObjectNode filterNode = objectMapper.createObjectNode();
        filterNode.put("property", "💽 Developer");
        filterNode.set("relation", relationNode);
        ObjectNode bodyNode = objectMapper.createObjectNode();
        bodyNode.set("filter", filterNode);
        return bodyNode;
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
