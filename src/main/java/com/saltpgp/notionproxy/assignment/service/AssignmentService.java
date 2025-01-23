package com.saltpgp.notionproxy.assignment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.saltpgp.notionproxy.assignment.model.Assignment;
import com.saltpgp.notionproxy.bucket.BucketApi;
import com.saltpgp.notionproxy.exceptions.NotionException;
import com.saltpgp.notionproxy.notionapi.NotionApiService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

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

    @SneakyThrows
    public Assignment getAssignment(String assignmentId) throws NotionException {
        Assignment assignment = getAssignmentFromCache("assignment_" + assignmentId, Assignment::fromJson);
        if(assignment != null) {
            return assignment;
        }
        assignment = extractAssignment(notionApiService.fetchPage(assignmentId));
        try{
        bucketApi.saveCache("assignment_" + assignmentId, Assignment.toJsonNode(assignment));
        }catch (Exception e){}
        return assignment;
    }

    @SneakyThrows
    public List<Assignment> getAssignmentsFromDeveloper(UUID developerId) throws NotionException {
        List<Assignment> assignmentsCache = getAssignmentFromCache("assignment_developer_" + developerId, Assignment::fromJsonList);;
        if(assignmentsCache != null) {
            return assignmentsCache;
        }

        List<Assignment> assignments = new ArrayList<>();
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
        bucketApi.saveCache("assignment_developer_" + developerId, Assignment.toJsonNode(assignments));
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

    public <T> T getAssignmentFromCache(String cacheKey, Function<String, T> fromJsonFunction) {
        JsonNode cache = bucketApi.getCache(cacheKey);
        if (cache != null) {
            try {
                return fromJsonFunction.apply(cache.toString());
            } catch (Exception e) {
                System.out.println("Error during JSON deserialization: " + e.getMessage());
            }
        }
        return null;
    }
}
