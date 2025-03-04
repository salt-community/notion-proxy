package com.saltpgp.notionproxy.modules.developer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.saltpgp.notionproxy.api.bucket.BucketApiService;
import com.saltpgp.notionproxy.modules.developer.model.Developer;
import com.saltpgp.notionproxy.modules.developer.model.DeveloperStatus;
import com.saltpgp.notionproxy.modules.developer.model.Responsible;
import com.saltpgp.notionproxy.exceptions.InvalidFilterException;
import com.saltpgp.notionproxy.exceptions.NotionException;
import com.saltpgp.notionproxy.exceptions.NotionNotFoundException;
import com.saltpgp.notionproxy.api.notion.NotionApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.saltpgp.notionproxy.modules.developer.service.DeveloperMapper.*;
import static com.saltpgp.notionproxy.modules.developer.service.DeveloperProperty.*;
import static com.saltpgp.notionproxy.api.notion.filter.NotionServiceFilters.filterBuilder;

@Service
@Slf4j
public class DeveloperService {

    private final NotionApiService notionApiService;
    private final BucketApiService bucketApiService;
    private final String DATABASE_ID;
    private final static String CACHE_ID = "developer_";
    private final static String INVALID_FILTER_ERROR_MESSAGE = "Invalid filter value: ";
    private final static String FILTER = """
                "filter": {
                    "property": "Status",
                    "select": {
                        "equals": "%s"
                    }
                }
            """;

    public DeveloperService(NotionApiService notionApiService, BucketApiService bucketApiService, @Value("${DEV_DATABASE_ID}") String DATABASE_ID) {
        this.notionApiService = notionApiService;
        this.bucketApiService = bucketApiService;
        this.DATABASE_ID = DATABASE_ID;
    }

    public List<Developer> getAllDevelopers(String filter, boolean useCache) throws NotionException, NotionNotFoundException {
        if (filter != null && !DeveloperStatus.isValid(filter)) {
            throw new InvalidFilterException(INVALID_FILTER_ERROR_MESSAGE + filter);
        }

        if (useCache) {
            JsonNode cache = bucketApiService.getCache(CACHE_ID + filter);
            try {
                if (cache != null) {
                    return Developer.fromJsonList(cache.toString());
                }
            } catch (Exception e) {
                log.warn("Failed to parse cached developers for ID: {}. Error: {}", filter, e.getMessage());
            }
        }
        log.debug("Cache miss for filter: {}. Fetching from Notion API.", filter);

        List<Developer> developers = new ArrayList<>();
        String nextCursor = null;
        boolean hasMore = true;
        while (hasMore) {
            log.debug("Fetching developer for filter: {} with cursor: {}", filter, nextCursor);
            JsonNode response = notionApiService.fetchDatabase(DATABASE_ID, filterBuilder(nextCursor, filter, FILTER));

            response.get(NotionObject.RESULTS).elements().forEachRemaining(page -> {
                if (page.get(Results.PROPERTIES).get(Properties.NAME).get(Name.TITLE).get(0) == null) return;
                developers.add(createDeveloperFromNotionPage(page));
            });

            nextCursor = response.get(NotionObject.NEXT_CURSOR).asText();
            hasMore = response.get(NotionObject.HAS_MORE).asBoolean();
            log.debug("Fetched {} developers so far for filter: {}", developers.size(), filter);
        }

        log.debug("Fetched total {} developers for filter: {}. Saving to cache.", developers.size(), filter);
        bucketApiService.saveCache(CACHE_ID + filter, Developer.toJsonNode(developers));
        return developers;
    }

    public Developer getDeveloperById(UUID id, boolean useCache) throws NotionException, NotionNotFoundException {
        if (useCache) {
            JsonNode cache = bucketApiService.getCache(CACHE_ID + id);
            try {
                if (cache != null) {
                    return Developer.fromJson(cache.toString());
                }
            } catch (Exception e) {
                log.warn("Failed to parse cached developer for ID: {}. Error: {}", id, e.getMessage());
            }
        }
        log.debug("Cache miss for developer ID: {}. Fetching from Notion API.", id);
        Developer developer = createDeveloperFromNotionPage(notionApiService.fetchPage(id.toString()));
        bucketApiService.saveCache(CACHE_ID + id, Developer.toJsonNode(developer));
        return developer;
    }

    private static Developer createDeveloperFromNotionPage(JsonNode page) {
        log.debug("Create developer with ID: {}", getDeveloperId(page));
        var properties = page.get(Results.PROPERTIES);
        var githubUrl = getDeveloperGithubUrl(properties);

        return new Developer(
                getDeveloperName(properties),
                UUID.fromString(getDeveloperId(page)),
                githubUrl,
                getDeveloperGithubImageUrl(githubUrl),
                getDeveloperEmail(properties),
                getDeveloperStatus(properties),
                getDeveloperTotalScore(properties),
                getResponsibleList(properties)
        );
    }

    private static List<Responsible> getResponsibleList(JsonNode properties) {
        List<Responsible> responsibleList = new ArrayList<>();
        properties.get(Properties.RESPONSIBLE).get(NotionResponsible.PEOPLE).elements().forEachRemaining(responsible -> {
            try {
                log.debug("Create responsible with ID: {}", getResponsibleId(responsible));
                responsibleList.add(new Responsible(
                        getResponsibleName(responsible),
                        getResponsibleId(responsible),
                        getResponsibleEmail(responsible)));
            } catch (Exception ignored) {
            }
        });
        return responsibleList;
    }
}
