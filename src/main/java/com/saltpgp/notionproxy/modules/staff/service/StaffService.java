package com.saltpgp.notionproxy.modules.staff.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.saltpgp.notionproxy.api.bucket.BucketApiService;
import com.saltpgp.notionproxy.exceptions.NotionException;
import com.saltpgp.notionproxy.api.notion.NotionApiService;
import com.saltpgp.notionproxy.api.notion.filter.NotionProperty.NotionPropertyFilter;
import com.saltpgp.notionproxy.api.notion.filter.NotionProperty.PeopleFilter;
import com.saltpgp.notionproxy.api.notion.filter.NotionServiceFilters;
import com.saltpgp.notionproxy.exceptions.NotionNotFoundException;
import com.saltpgp.notionproxy.modules.staff.model.Staff;
import com.saltpgp.notionproxy.modules.staff.model.Consultant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.saltpgp.notionproxy.modules.staff.service.StaffMapper.*;
import static com.saltpgp.notionproxy.modules.staff.service.StaffProperty.*;

@Service
@Slf4j
public class StaffService {

    private final NotionApiService notionApiService;
    private final String CORE_DATABASE_ID, DEV_DATABASE_ID;
    private final BucketApiService BUCKET_API;

    public StaffService(NotionApiService notionApiService, @Value("${CORE_DATABASE_ID}") String coreDatabaseId, @Value("${DEV_DATABASE_ID}") String devDatabaseId,
                        BucketApiService bucketApiService) {
        this.notionApiService = notionApiService;
        CORE_DATABASE_ID = coreDatabaseId;
        DEV_DATABASE_ID = devDatabaseId;
        BUCKET_API = bucketApiService;
    }

    public List<Staff> getAllCore(String filter, boolean useCache) throws NotionException, NotionNotFoundException {
        if(useCache){
            JsonNode cache = BUCKET_API.getCache(CACHE_ID + filter);
            try {
                return Staff.fromJsonList(cache.toString());
            } catch (Exception e) {
                log.warn("Failed to parse cached staff for filter: {}. Error: {}", filter, e.getMessage());
            }
        }

        String nextCursor = null;
        boolean hasMore = true;
        List<Staff> staffList = new ArrayList<>();
        while (hasMore) {
            log.debug("Getting staff list started new loop, using filter {}", filter);
            JsonNode response = notionApiService.fetchDatabase(CORE_DATABASE_ID,
                    NotionServiceFilters.filterBuilder(nextCursor, filter, StaffFilter.STAFF_FILTER));

            response.get(NotionObject.RESULTS).elements().forEachRemaining(element -> {
                Staff staff = createStaffFromNotionPage(element);
                if(staff != null)
                    staffList.add(staff);
            });

            nextCursor = response.get(NotionObject.NEXT_CURSOR).asText();
            hasMore = response.get(NotionObject.HAS_MORE).asBoolean();
        }
        BUCKET_API.saveCache(CACHE_ID + filter, Staff.toJsonNode(staffList));
        return staffList;
    }

    public Staff getStaffById(UUID id, boolean useCache) throws NotionException, NotionNotFoundException {
        NotionPropertyFilter filter = NotionPropertyFilter.peopleFilter(PeopleFilter.CONTAINS, id.toString(), "Person");
        if(useCache) {
            JsonNode cache = BUCKET_API.getCache(CACHE_ID + id);
            try {
                return Staff.fromJson(cache.toString());
            } catch (Exception e) {
                log.warn("Failed to parse cached staff for filter: {}. Error: {}", filter, e.getMessage());
            }
        }
        log.debug("Fetching staff by id: {}", filter);
        JsonNode response = notionApiService.fetchDatabase(CORE_DATABASE_ID,
                NotionServiceFilters.filterBuilder(null, filter));

        Staff staff = createStaffFromNotionPage(response.get(NotionObject.RESULTS).get(0));
        BUCKET_API.saveCache(CACHE_ID + id, Staff.toJsonNode(staff));
        return staff;

    }

    public List<Consultant> getStaffConsultants(UUID id, boolean useCache) throws NotionException, NotionNotFoundException {
        String nextCursor = null;
        boolean hasMore = true;
        List<Consultant> devs = new ArrayList<>();
        if(useCache) {
            JsonNode cache = BUCKET_API.getCache(CACHE_ID_CONSULTANTS + id.toString());
            try {
                return Consultant.fromJsonList(cache.toString());
            } catch (Exception e) {
                log.warn("Failed to parse cached Consultant for filter: {}. Error: {}", id, e.getMessage());
            }
        }
        log.debug("Fetching staff consultants by id: {}", id);
        while (hasMore) {
            JsonNode response = notionApiService.fetchDatabase(DEV_DATABASE_ID,
                    NotionServiceFilters.filterBuilder(nextCursor, id.toString(), StaffFilter.STAFF_FILTER_RESPONSIBLE));

            response.get(NotionObject.RESULTS).forEach(element -> {
                devs.add(createConsultantFromNotionPage(element));
            });

            nextCursor = response.get(NotionObject.NEXT_CURSOR).asText();
            hasMore = response.get(NotionObject.HAS_MORE).asBoolean();
        }
        BUCKET_API.saveCache(CACHE_ID_CONSULTANTS + id, Consultant.toJsonNode(devs));
        return devs;
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

    private Consultant createConsultantFromNotionPage(JsonNode page) {
        return new Consultant(getConsultantName(page), getConsultantEmail(page), getConsultantId(page));
    }
}
