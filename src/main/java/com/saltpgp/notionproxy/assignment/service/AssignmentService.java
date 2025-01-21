package com.saltpgp.notionproxy.assignment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.saltpgp.notionproxy.assignment.model.Assignment;
import com.saltpgp.notionproxy.exceptions.NotionException;
import com.saltpgp.notionproxy.notionapi.NotionApiService;
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
        Assignment foundAssignment = null;
        String nextCursor = null;
        boolean hasMore = true;

        ObjectNode bodyNode = getDeveloperNode(developerId);

        while (hasMore) {
            if (nextCursor != null) {
                bodyNode.put("start_cursor", nextCursor);
            }

            JsonNode scoreResponse = notionApiService.fetchDatabase(SCORE_DATABASE_ID, bodyNode);

            if (scoreResponse != null && scoreResponse.has("results")) {
                for (JsonNode element : scoreResponse.get("results")) {
                    if (element.has("id") ) {
                        String elementId = element.get("id").asText();
                        if (assignmentId.toString().equals(elementId)) {
                            foundAssignment = extractScore(element);
                            break;
                        }
                    }
                }
            }

            nextCursor = scoreResponse.has("next_cursor") && !scoreResponse.get("next_cursor").isNull()
                    ? scoreResponse.get("next_cursor").asText()
                    : null;

            hasMore = scoreResponse.has("has_more") && scoreResponse.get("has_more").asBoolean();

            if (foundAssignment != null) {
                break;
            }
        }

        return foundAssignment;
    }


    public List<Assignment> getAssignmentsFromDeveloper(UUID id) throws NotionException {
        List<Assignment> allScores = new ArrayList<>();
        String nextCursor = null;
        boolean hasMore = true;
        ObjectNode bodyNode = getDeveloperNode(id);
        while (hasMore) {
            if (nextCursor != null) {
                bodyNode.put("start_cursor", nextCursor);
            }
            JsonNode scoreResponse = notionApiService.fetchDatabase(SCORE_DATABASE_ID, bodyNode);


            scoreResponse.get("results").elements().forEachRemaining(element -> {
                Assignment score = extractScore(element);
                if (score != null) {
                    allScores.add(score);
                }
            });
            nextCursor = scoreResponse.get("next_cursor").asText();
            hasMore = scoreResponse.get("has_more").asBoolean();
        }

        return allScores;
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

//    private JsonNode createQueryRequestBody(String nextCursor) {
//        ObjectMapper objectMapper = new ObjectMapper();
//        ObjectNode body = objectMapper.createObjectNode();
//        if (nextCursor != null) {
//            body.put("start_cursor", nextCursor);
//        }
//        System.out.println("body = " + body);
//        return body;
//    }

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
