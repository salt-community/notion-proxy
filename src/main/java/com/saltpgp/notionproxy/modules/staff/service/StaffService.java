package com.saltpgp.notionproxy.modules.staff.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.saltpgp.notionproxy.api.bucket.BucketApiService;
import com.saltpgp.notionproxy.exceptions.NotionException;
import com.saltpgp.notionproxy.exceptions.NotionNotFoundException;
import com.saltpgp.notionproxy.api.notion.NotionApiService;
import com.saltpgp.notionproxy.api.notion.filter.NotionProperty.NotionPropertyFilter;
import com.saltpgp.notionproxy.api.notion.filter.NotionProperty.PeopleFilter;
import com.saltpgp.notionproxy.api.notion.filter.NotionServiceFilters;
import com.saltpgp.notionproxy.modules.staff.model.Staff;
import com.saltpgp.notionproxy.modules.staff.model.StaffDev;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class StaffService {

    private final NotionApiService notionApiService;
    private final String CORE_DATABASE_ID, DEV_DATABASE_ID;
    private final BucketApiService BUCKET_API;
    private final String BUCKET_ID_ALL = "staff_getAll_filter_";
    private final String BUCKET_ID_DEV = "staff_getAll_dev_id_";
    private final String BUCKET_ID_SINGLE = "staff_getAll_single";

    public StaffService(NotionApiService notionApiService, @Value("${CORE_DATABASE_ID}") String coreDatabaseId, @Value("${DATABASE_ID}") String devDatabaseId
            , BucketApiService bucketApiService) {
        this.notionApiService = notionApiService;
        CORE_DATABASE_ID = coreDatabaseId;
        DEV_DATABASE_ID = devDatabaseId;
        BUCKET_API = bucketApiService;
    }

    public List<Staff> getAllCore(String filter) throws NotionException {
        String nextCursor = null;
        boolean hasMore = true;
        JsonNode cache = BUCKET_API.getCache(BUCKET_ID_ALL + filter);
        try {
            return Staff.fromJsonList(cache.toString());
        } catch (Exception e) {
            log.warn("Failed to parse cached staff for filter: {}. Error: {}", filter, e.getMessage());
        }
        List<Staff> staffList = new ArrayList<>();
        while (hasMore) {
            log.debug("Getting staff list started new loop, using filter {}", filter);
            JsonNode response = notionApiService.fetchDatabase(CORE_DATABASE_ID,
                    NotionServiceFilters.filterBuilder(nextCursor, filter, StaffFilter.STAFF_FILTER));
            response.get("results").elements().forEachRemaining(element -> {
                JsonNode person = element.get("properties").get("Person").get("people").get(0);
                if (person == null) {
                    log.debug("Skipped empty person");
                    return;
                }

                staffList.add(new Staff(
                        person.get("name").asText(),
                        person.get("person").get("email").asText(),
                        UUID.fromString(person.get("id").asText()),
                        element.get("properties").get("Guild").get("multi_select").get(0).get("name").asText()
                ));
            });
            nextCursor = response.get("next_cursor").asText();
            hasMore = response.get("has_more").asBoolean();
        }
        BUCKET_API.saveCache(BUCKET_ID_ALL + filter, Staff.toJsonNode(staffList));
        return staffList;
    }

    public Staff getStaffById(UUID id) throws NotionException, NotionNotFoundException {
        NotionPropertyFilter filter = NotionPropertyFilter.peopleFilter(PeopleFilter.CONTAINS, id.toString(), "Person");
        JsonNode cache = BUCKET_API.getCache(BUCKET_ID_SINGLE + id.toString());
        try {
            return Staff.fromJson(cache.toString());
        } catch (Exception e) {
            log.warn("Failed to parse cached staff for filter: {}. Error: {}", filter, e.getMessage());
        }
        log.debug("Fetching staff by id: {}", filter);
        JsonNode response = notionApiService.fetchDatabase(CORE_DATABASE_ID,
                NotionServiceFilters.filterBuilder(null, filter));
        try {
            JsonNode element = response.get("results").get(0);
            JsonNode person = element.get("properties").get("Person").get("people").get(0);
            Staff staff = new Staff(
                    person.get("name").asText(),
                    person.get("person").get("email").asText(),
                    UUID.fromString(person.get("id").asText()),
                    element.get("properties").get("Guild").get("multi_select").get(0).get("name").asText());
            BUCKET_API.saveCache(BUCKET_ID_SINGLE + id.toString(), Staff.toJsonNode(staff));
            return staff;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new NotionNotFoundException();
        }

    }

    public List<StaffDev> getStaffConsultants(UUID id) throws NotionException {
        String nextCursor = null;
        boolean hasMore = true;
        List<StaffDev> devs = new ArrayList<>();
        JsonNode cache = BUCKET_API.getCache(BUCKET_ID_DEV + id.toString());
        try {
            return StaffDev.fromJsonList(cache.toString());
        } catch (Exception e) {
            log.warn("Failed to parse cached staffdev for filter: {}. Error: {}", id.toString(), e.getMessage());
        }
        log.debug("Fetching staff consultants by id: {}", id);
        while (hasMore) {
            JsonNode response = notionApiService.fetchDatabase(DEV_DATABASE_ID,
                    NotionServiceFilters.filterBuilder(nextCursor, id.toString(), StaffFilter.STAFF_FILTER_RESPONSIBLE));
            response.get("results").forEach(element -> {
                devs.add(getStaffFromPage(element));
            });
            nextCursor = response.get("next_cursor").asText();
            hasMore = response.get("has_more").asBoolean();
        }
        BUCKET_API.saveCache(BUCKET_ID_DEV + id.toString(), StaffDev.toJsonNode(devs));
        return devs;
    }


    private StaffDev getStaffFromPage(JsonNode page) {
        String name = page.get("properties").get("Name").get("title").get(0).get("plain_text").asText();
        String email = StaffMapper.getDevEmail(page);
        UUID id = UUID.fromString(page.get("id").asText());
        return new StaffDev(name, email, id);
    }
}
