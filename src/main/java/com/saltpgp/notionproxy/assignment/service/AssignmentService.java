package com.saltpgp.notionproxy.assignment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.saltpgp.notionproxy.assignment.model.Assignment;
import com.saltpgp.notionproxy.bucket.BucketApi;
import com.saltpgp.notionproxy.exceptions.NotionException;
import com.saltpgp.notionproxy.notionapi.NotionApiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.saltpgp.notionproxy.assignment.service.AssignmentMapper.*;
import static com.saltpgp.notionproxy.service.NotionServiceFilters.filterBuilder;
import com.saltpgp.notionproxy.assignment.service.AssignmentProperty.*;

@Service
public class AssignmentService {

    private final NotionApiService notionApiService;
    private final BucketApi bucketApi;
    private final String SCORE_DATABASE_ID;

    public static final String FILTER = """
            "filter": {
                "property": "💽 Developer",
                "relation": {
                    "contains": "%s"
                }
            }
            """;

    public AssignmentService(NotionApiService notionApiService, BucketApi bucketApi, @Value("${SCORE_DATABASE_ID}")String scoreDatabaseId) {
        this.notionApiService = notionApiService;
        this.bucketApi = bucketApi;
        SCORE_DATABASE_ID = scoreDatabaseId;
    }

    public Assignment getAssignment(String assignmentId) throws NotionException {
        JsonNode cache = bucketApi.getCache("assignment_" + assignmentId);
        if(cache != null) {
            return extractAssignment(cache);
        }
        JsonNode response = notionApiService.fetchPage(assignmentId);
        bucketApi.saveCache("assignment_" + assignmentId, response);
        return extractAssignment(response);
    }

    public List<Assignment> getAssignmentsFromDeveloper(UUID developerId) throws NotionException {
        List<Assignment> assignments = new ArrayList<>();

        JsonNode cache = bucketApi.getCache("assignment_developer_" + developerId);
        if(cache != null) {
            cache.get(NotionObject.RESULTS).elements().forEachRemaining(elements -> {
                assignments.add(extractAssignment(elements));
            });
            return assignments;
        }

        String nextCursor = null;
        boolean hasMore = true;

        while (hasMore) {
            JsonNode response = notionApiService.fetchDatabase(
                    SCORE_DATABASE_ID, filterBuilder(nextCursor, developerId.toString(), FILTER));

            response.get(NotionObject.RESULTS).elements().forEachRemaining(elements -> {
                assignments.add(extractAssignment(elements));
            });

            nextCursor = response.get(NotionObject.NEXT_CURSOR).asText();
            hasMore = response.get(NotionObject.HAS_MORE).asBoolean();
        }
        return assignments;
    }

    private Assignment extractAssignment(JsonNode elements) {
        var properties = elements.get(Results.PROPERTIES);
        return new Assignment(
                elements.get(Results.ID).asText(),
                getName(properties),
                getScore(properties),
                getCategories(properties),
                getScoreComment(properties));
    }
}
