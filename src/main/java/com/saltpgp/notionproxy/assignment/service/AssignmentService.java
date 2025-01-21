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

import static com.saltpgp.notionproxy.assignment.service.AssignmentMapper.*;
import static com.saltpgp.notionproxy.service.NotionServiceFilters.filterBuilder;
import com.saltpgp.notionproxy.assignment.service.AssignmentProperty.*;

@Service
public class AssignmentService {

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


    public Assignment getAssignmentFromDeveloper(String assignmentId) throws NotionException {
        JsonNode response = notionApiService.fetchPage(assignmentId);
        return extractAssignment(response);
    }

    public List<Assignment> getAssignmentsFromDeveloper(UUID developerId) throws NotionException {
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
