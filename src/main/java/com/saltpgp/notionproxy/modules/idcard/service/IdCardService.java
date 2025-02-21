package com.saltpgp.notionproxy.modules.idcard.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.saltpgp.notionproxy.api.bucket.BucketApiService;
import com.saltpgp.notionproxy.api.notion.NotionApiService;
import com.saltpgp.notionproxy.exceptions.NotionException;
import com.saltpgp.notionproxy.exceptions.NotionNotFoundException;
import com.saltpgp.notionproxy.modules.idcard.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.saltpgp.notionproxy.api.notion.filter.NotionServiceFilters.filterBuilder;
import static com.saltpgp.notionproxy.modules.staff.service.StaffProperty.CACHE_ID;

@Service
@Slf4j
public class IdCardService {
    private final NotionApiService notionApiService;
    private final BucketApiService bucketApiService;
    private final String DATABASE_ID;

    private static final String FILTER = """
                "filter": {
                    "property": "Email",
                    "email": {
                        "equals": "%s"
                    }
                }
            """;

    public IdCardService(NotionApiService notionApiService, BucketApiService bucketApiService, @Value("${DATABASE_ID}") String DATABASE_ID) {
        this.notionApiService = notionApiService;
        this.bucketApiService = bucketApiService;
        this.DATABASE_ID = DATABASE_ID;
    }

    public User getIdCard(String email, boolean useCache) throws NotionException, NotionNotFoundException {
        if (useCache) {
            JsonNode cache = bucketApiService.getCache(CACHE_ID + email);
            try {
                if (cache != null) {
                    return User.fromJson(cache.toString());
                }
            } catch (Exception e) {
                log.warn("Failed to parse cached user for email: {}. Error: {}", email, e.getMessage());
            }
        }
        log.debug("Fetching dev by email: {}", email);
        JsonNode response = notionApiService.fetchDatabase(DATABASE_ID, filterBuilder(null, email, FILTER));
        JsonNode properties = response.get("result").get(0).get("properties");
        User user = new User(
                properties.get("Name").get("title").get("text").get("content").asText(),
                properties.get("Course").get("select").get("name").asText(),
                properties.get("Payroll End Date").get("date").asText(),
                properties.get("Status").get("select").get("name").asText(),
                properties.get("GitHub").get("url").asText()
        );
        bucketApiService.saveCache(CACHE_ID + email, User.toJsonNode(user));
        return user;
    }
}
