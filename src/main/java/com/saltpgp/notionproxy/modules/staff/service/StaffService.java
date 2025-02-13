package com.saltpgp.notionproxy.modules.staff.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.saltpgp.notionproxy.api.bucket.BucketApiService;
import com.saltpgp.notionproxy.exceptions.NotionException;
import com.saltpgp.notionproxy.api.notion.NotionApiService;
import com.saltpgp.notionproxy.api.notion.filter.NotionProperty.NotionPropertyFilter;
import com.saltpgp.notionproxy.api.notion.filter.NotionProperty.PeopleFilter;
import com.saltpgp.notionproxy.api.notion.filter.NotionServiceFilters;
import com.saltpgp.notionproxy.modules.staff.model.Staff;
import com.saltpgp.notionproxy.modules.staff.model.Consultant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.saltpgp.notionproxy.modules.staff.service.StaffMapper.*;

@Service
@Slf4j
public class StaffService {

    private final NotionApiService notionApiService;
    private final String CORE_DATABASE_ID, DEV_DATABASE_ID;
    private final BucketApiService BUCKET_API;
    private final static String CACHE_ID = "staff_";
    private final static String CACHE_ID_CONSULTANTS = "staff_consultants_";

    public StaffService(NotionApiService notionApiService, @Value("${CORE_DATABASE_ID}") String coreDatabaseId, @Value("${DATABASE_ID}") String devDatabaseId,
                        BucketApiService bucketApiService) {
        this.notionApiService = notionApiService;
        CORE_DATABASE_ID = coreDatabaseId;
        DEV_DATABASE_ID = devDatabaseId;
        BUCKET_API = bucketApiService;
    }

    public List<Staff> getAllCore(String filter) throws NotionException {
        String nextCursor = null;
        boolean hasMore = true;
        JsonNode cache = BUCKET_API.getCache(CACHE_ID + filter);
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
                Staff staff = createStaffFromNotionPage(element);
                if(staff == null)
                    return;
                staffList.add(staff);
            });
            nextCursor = response.get("next_cursor").asText();
            hasMore = response.get("has_more").asBoolean();
        }
        BUCKET_API.saveCache(CACHE_ID + filter, Staff.toJsonNode(staffList));
        return staffList;
    }

    public Staff getStaffById(UUID id) throws NotionException {
        NotionPropertyFilter filter = NotionPropertyFilter.peopleFilter(PeopleFilter.CONTAINS, id.toString(), "Person");
        JsonNode cache = BUCKET_API.getCache(CACHE_ID + id);
        try {
            return Staff.fromJson(cache.toString());
        } catch (Exception e) {
            log.warn("Failed to parse cached staff for filter: {}. Error: {}", filter, e.getMessage());
        }
        log.debug("Fetching staff by id: {}", filter);
        JsonNode response = notionApiService.fetchDatabase(CORE_DATABASE_ID,
                NotionServiceFilters.filterBuilder(null, filter));

        Staff staff = createStaffFromNotionPage(response.get("results").get(0));
        BUCKET_API.saveCache(CACHE_ID + id, Staff.toJsonNode(staff));
        return staff;

    }

    private static Staff createStaffFromNotionPage(JsonNode element) {
        JsonNode person = getStaffPerson(element);
        if (person == null) {
            log.debug("Skipped empty person");
            return null;
        }
        return new Staff(
                getStaffName(person),
                getStaffEmail(person),
                getStaffId(person),
                getStaffRole(element));
    }

    public List<Consultant> getStaffConsultants(UUID id) throws NotionException {
        String nextCursor = null;
        boolean hasMore = true;
        List<Consultant> devs = new ArrayList<>();
        JsonNode cache = BUCKET_API.getCache(CACHE_ID_CONSULTANTS + id.toString());
        try {
            return Consultant.fromJsonList(cache.toString());
        } catch (Exception e) {
            log.warn("Failed to parse cached Consultant for filter: {}. Error: {}", id, e.getMessage());
        }
        log.debug("Fetching staff consultants by id: {}", id);
        while (hasMore) {
            JsonNode response = notionApiService.fetchDatabase(DEV_DATABASE_ID,
                    NotionServiceFilters.filterBuilder(nextCursor, id.toString(), StaffFilter.STAFF_FILTER_RESPONSIBLE));
            response.get("results").forEach(element -> {
                devs.add(createConsultantFromNotionPage(element));
            });
            nextCursor = response.get("next_cursor").asText();
            hasMore = response.get("has_more").asBoolean();
        }
        BUCKET_API.saveCache(CACHE_ID_CONSULTANTS + id, Consultant.toJsonNode(devs));
        return devs;
    }

    private Consultant createConsultantFromNotionPage(JsonNode page) {
        String name = page.get("properties").get("Name").get("title").get(0).get("plain_text").asText();
        String email = StaffMapper.getDevEmail(page);
        UUID id = UUID.fromString(page.get("id").asText());
        return new Consultant(name, email, id);
    }
}
