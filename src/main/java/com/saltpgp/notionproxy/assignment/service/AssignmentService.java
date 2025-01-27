package com.saltpgp.notionproxy.assignment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.saltpgp.notionproxy.assignment.model.Assignment;
import com.saltpgp.notionproxy.bucket.BucketApi;
import com.saltpgp.notionproxy.exceptions.NotionException;
import com.saltpgp.notionproxy.notionapi.NotionApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.saltpgp.notionproxy.assignment.service.AssignmentMapper.*;
import static com.saltpgp.notionproxy.service.NotionServiceFilters.filterBuilder;
import com.saltpgp.notionproxy.assignment.service.AssignmentProperty.*;

@Service
@Slf4j
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

    public AssignmentService(NotionApiService notionApiService, BucketApi bucketApi, @Value("${SCORE_DATABASE_ID}") String scoreDatabaseId) {
        this.notionApiService = notionApiService;
        this.bucketApi = bucketApi;
        SCORE_DATABASE_ID = scoreDatabaseId;
    }

    public Assignment getAssignment(String assignmentId, boolean useCache) throws NotionException {
        log.debug("Starting to fetch assignment with ID: {}", assignmentId);
        if (useCache) {
            JsonNode cache = bucketApi.getCache("assignment_" + assignmentId);

            try {
                if (cache != null) {
                    log.debug("Cache hit for assignment ID: {}", assignmentId);
                    return Assignment.fromJson(cache.toString());
                }
            } catch (Exception e) {
                log.warn("Failed to parse cached assignment for ID: {}. Error: {}", assignmentId, e.getMessage());
            }
        }

        log.debug("Cache miss for assignment ID: {}. Fetching from Notion API.", assignmentId);
        Assignment assignment = extractAssignment(notionApiService.fetchPage(assignmentId));

        log.debug("Saving fetched assignment ID: {} to cache.", assignmentId);
        bucketApi.saveCache("assignment_" + assignmentId, Assignment.toJsonNode(assignment));
        return assignment;
    }

    public List<Assignment> getAssignmentsFromDeveloper(UUID developerId, boolean useCache) throws NotionException {
        log.debug("Starting to fetch assignments for developer ID: {}", developerId);
        JsonNode cache = bucketApi.getCache("assignment_developer_" + developerId);
        if (useCache) {
            try {
                if (cache != null) {
                    log.warn("Cache hit for assignments of developer ID: {}", developerId);
                    return Assignment.fromJsonList(cache.toString());
                }
            } catch (Exception e) {
                log.debug("Failed to parse cached assignments for developer ID: {}. Error: {}", developerId, e.getMessage());
            }
        }
        log.debug("Cache miss for developer ID: {}. Fetching from Notion API.", developerId);
        List<Assignment> assignments = new ArrayList<>();
        String nextCursor = null;
        boolean hasMore = true;

        while (hasMore) {
            log.debug("Fetching assignments for developer ID: {} with cursor: {}", developerId, nextCursor);

            JsonNode response = notionApiService.fetchDatabase(
                    SCORE_DATABASE_ID, filterBuilder(nextCursor, developerId.toString(), FILTER));

            response.get(NotionObject.RESULTS).elements().forEachRemaining(elements -> {
                assignments.add(extractAssignment(elements));
            });

            nextCursor = response.get(NotionObject.NEXT_CURSOR).asText();
            hasMore = response.get(NotionObject.HAS_MORE).asBoolean();

            log.debug("Fetched {} assignments so far for developer ID: {}", assignments.size(), developerId);
        }

        log.debug("Fetched total {} assignments for developer ID: {}. Saving to cache.", assignments.size(), developerId);
        bucketApi.saveCache("assignment_developer_" + developerId, Assignment.toJsonNode(assignments));
        return assignments;
    }

    private Assignment extractAssignment(JsonNode elements) {
        log.debug("Extracting assignment from JSON elements.");
        var properties = elements.get(Results.PROPERTIES);

        Assignment assignment = new Assignment(
                elements.get(Results.ID).asText(),
                getName(properties),
                getScore(properties),
                getCategories(properties),
                getScoreComment(properties)
        );

        log.debug("Extracted assignment with ID: {}", assignment.getId());
        return assignment;
    }
}
