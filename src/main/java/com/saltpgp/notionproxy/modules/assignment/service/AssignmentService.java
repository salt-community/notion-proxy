package com.saltpgp.notionproxy.modules.assignment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.saltpgp.notionproxy.modules.assignment.model.Assignment;
import com.saltpgp.notionproxy.api.bucket.BucketApiService;
import com.saltpgp.notionproxy.exceptions.NotionException;
import com.saltpgp.notionproxy.api.notion.NotionApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.saltpgp.notionproxy.modules.assignment.service.AssignmentMapper.*;
import static com.saltpgp.notionproxy.api.notion.filter.NotionServiceFilters.filterBuilder;
import com.saltpgp.notionproxy.modules.assignment.service.AssignmentProperty.*;

@Service
@Slf4j
public class AssignmentService {

    private final NotionApiService notionApiService;
    private final BucketApiService bucketApiService;
    private final String SCORE_DATABASE_ID;
    private final static String CACHE_ID_DEVELOPER = "assignment_developer_";
    private final static String CACHE_ID_SINGLE = "assignment_";

    public static final String FILTER = """
            "filter": {
                "property": "💽 Developer",
                "relation": {
                    "contains": "%s"
                }
            }
            """;

    public AssignmentService(NotionApiService notionApiService, BucketApiService bucketApiService, @Value("${SCORE_DATABASE_ID}") String scoreDatabaseId) {
        this.notionApiService = notionApiService;
        this.bucketApiService = bucketApiService;
        SCORE_DATABASE_ID = scoreDatabaseId;
    }

    public Assignment getAssignment(String assignmentId, boolean useCache) throws NotionException {
        if (useCache) {
            JsonNode cache = bucketApiService.getCache(CACHE_ID_SINGLE+ assignmentId);
            try {
                if (cache != null) {
                    return Assignment.fromJson(cache.toString());
                }
            } catch (Exception e) {
                log.warn("Failed to parse cached assignment for ID: {}. Error: {}", assignmentId, e.getMessage());
            }
        }

        log.debug("Cache miss for assignment ID: {}. Fetching from Notion API.", assignmentId);
        Assignment assignment = extractAssignment(notionApiService.fetchPage(assignmentId));

        bucketApiService.saveCache("CACHE_ID_SINGLE" + assignmentId, Assignment.toJsonNode(assignment));
        return assignment;
    }

    public List<Assignment> getAssignmentsFromDeveloper(UUID developerId, boolean useCache) throws NotionException {
        if (useCache) {
            JsonNode cache = bucketApiService.getCache(CACHE_ID_DEVELOPER + developerId);
            try {
                if (cache != null) {
                    return Assignment.fromJsonList(cache.toString());
                }
            } catch (Exception e) {
                log.warn("Failed to parse cached assignments for developer ID: {}. Error: {}", developerId, e.getMessage());
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
        bucketApiService.saveCache(CACHE_ID_DEVELOPER + developerId, Assignment.toJsonNode(assignments));
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
