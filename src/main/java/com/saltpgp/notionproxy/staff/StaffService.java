package com.saltpgp.notionproxy.staff;

import com.fasterxml.jackson.databind.JsonNode;
import com.saltpgp.notionproxy.exceptions.NotionException;
import com.saltpgp.notionproxy.service.NotionApiService;
import com.saltpgp.notionproxy.service.NotionServiceFilters;
import com.saltpgp.notionproxy.staff.models.Staff;
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
    private final String CORE_DATABASE_ID;

    public StaffService(NotionApiService notionApiService, @Value("${CORE_DATABASE_ID}")String coreDatabaseId) {
        this.notionApiService = notionApiService;
        CORE_DATABASE_ID = coreDatabaseId;
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
        System.out.println(NotionServiceFilters.filterBuilder(null, id.toString(), StaffFilter.STAFF_FILTER_SINGLE));
        JsonNode response = notionApiService.fetchDatabase(CORE_DATABASE_ID,
                NotionServiceFilters.filterBuilder("none", id.toString(), StaffFilter.STAFF_FILTER_SINGLE));
        System.out.println(response);
        return new Staff("name", "email",UUID.randomUUID(), "mastaer");
//        return new Staff(properties.get("Name").get("title").get(0).get("plain_text").asText()
//                ,properties.get("Person").get("people").get(0).get("person").get("email").asText(),
//                id,
//                properties.get("Guild").get("multi_select").get(0).get("name").asText());
    }
}
