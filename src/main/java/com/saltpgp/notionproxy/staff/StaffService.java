package com.saltpgp.notionproxy.staff;

import com.fasterxml.jackson.databind.JsonNode;
import com.saltpgp.notionproxy.exceptions.NotionException;
import com.saltpgp.notionproxy.service.NotionApiService;
import com.saltpgp.notionproxy.service.NotionServiceFilters;
import com.saltpgp.notionproxy.staff.models.Staff;
import com.saltpgp.notionproxy.staff.models.StaffDev;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class StaffService {

    private NotionApiService notionApiService;
    private final String CORE_DATABASE_ID, DEV_DATABASE_ID;

    public StaffService(NotionApiService notionApiService, @Value("${CORE_DATABASE_ID}")String coreDatabaseId, @Value("${DATABASE_ID}") String devDatabaseId) {
        this.notionApiService = notionApiService;
        CORE_DATABASE_ID = coreDatabaseId;
        DEV_DATABASE_ID = devDatabaseId;
    }

    public List<Staff> getAllCore(String filter) throws NotionException {
        String nextCursor = null;
        boolean hasMore = true;
        List<Staff> staffList= new ArrayList<>();
        while(hasMore) {
            JsonNode response = notionApiService.fetchDatabase(CORE_DATABASE_ID,
                    NotionServiceFilters.filterBuilder(nextCursor,filter,StaffFilter.STAFF_FILTER));
            response.get("results").elements().forEachRemaining(element -> {
                JsonNode person = element.get("properties").get("Person").get("people").get(0);
                if (person == null) {
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
        return staffList;
    }

    public Staff getStaffById(UUID id) throws NotionException {
        JsonNode response = notionApiService.fetchDatabase(CORE_DATABASE_ID,
                NotionServiceFilters.filterBuilder(null, id.toString(), StaffFilter.STAFF_FILTER_SINGLE));
        JsonNode element = response.get("results").get(0);
        JsonNode person = element.get("properties").get("Person").get("people").get(0);
        return new Staff(
                person.get("name").asText(),
                person.get("person").get("email").asText(),
                UUID.fromString(person.get("id").asText()),
                element.get("properties").get("Guild").get("multi_select").get(0).get("name").asText());
    }

    public List<StaffDev> getStaffConsultants(UUID id) throws NotionException {
        JsonNode response = notionApiService.fetchDatabase(DEV_DATABASE_ID,
                NotionServiceFilters.filterBuilder(null, id.toString(), StaffFilter.STAFF_FILTER_RESPONSIBLE));
        List<StaffDev> devs = new ArrayList<>();
        response.get("results").forEach(element -> {
            devs.add(getStaffFromPage(element));
        });
        return devs;
    }


    private StaffDev getStaffFromPage(JsonNode page) {
        String name = page.get("properties").get("Name").get("title").get(0).get("plain_text").asText();
        String email = "";
        UUID id = UUID.fromString(page.get("id").asText());
        return new StaffDev(name, email, id);
    }
}
