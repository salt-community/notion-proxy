package com.saltpgp.notionproxy.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.saltpgp.notionproxy.exceptions.NotionException;
import com.saltpgp.notionproxy.models.Consultant;
import com.saltpgp.notionproxy.models.ResponsiblePerson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class NotionService {

    private final RestClient restClient;

    @Value("${NOTION_API_KEY}")
    private String API_KEY;

    @Value("${DATABASE_ID}")
    private String DATABASE_ID;

    @Value("${NOTION_VERSION}")
    private String NOTION_VERSION;

    public NotionService() {
        restClient = RestClient.builder().baseUrl("https://api.notion.com/v1").build();
    }

    public Consultant getResponsiblePersonNameByUserId(UUID id) throws NotionException {
        JsonNode response = restClient
                .get()
                .uri(String.format("/pages/%s", id))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + API_KEY)
                .header("Notion-Version", NOTION_VERSION)
                .retrieve()
                .body(JsonNode.class);

        if (response == null) {
            throw new NotionException();
        }

        if ((response.get("properties").get("Responsible").get("people").get(0)) == null) {
            return null;
        }

        if (response.get("properties").get("Name").get("title").get(0) == null) {
            return null;
        }

        List<ResponsiblePerson> responsiblePersonList = getResponsiblePersonsFromResponse(response, false);

        return new Consultant(
                response.get("properties").get("Name").get("title").get(0).get("plain_text").asText(),
                UUID.fromString(response.get("id").asText()),
                responsiblePersonList);
    }

    private List<ResponsiblePerson> getResponsiblePersonsFromResponse(JsonNode response, boolean includeNull) {
        List<ResponsiblePerson> responsiblePersonList = new ArrayList<>();
        if (response.get("properties").get("Responsible").get("people").get(0) != null) {
            response.get("properties").get("Responsible").get("people").elements().forEachRemaining(element2 -> {
                String name = null;
                if (element2.get("name") != null) {
                    name = element2.get("name").asText();
                } else if (!includeNull) {
                    return;
                }
                ResponsiblePerson responsiblePerson = new ResponsiblePerson(
                        name,
                        UUID.fromString(element2.get("id").asText()));
                responsiblePersonList.add(responsiblePerson);
            });
        }
        return responsiblePersonList;
    }

    public List<Consultant> getConsultants(boolean includeEmpty, boolean includeNull) throws NotionException {
        List<Consultant> allConsultants = new ArrayList<>();
        String nextCursor = null;
        boolean hasMore = true;

        while (hasMore) {
            JsonNode response = restClient
                    .post()
                    .uri(String.format("/databases/%s/query", DATABASE_ID))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Notion-Version", NOTION_VERSION)
                    .body(createQueryRequestBody(nextCursor))
                    .retrieve()
                    .body(JsonNode.class);

            if (response == null) {
                throw new NotionException();
            }

            response.get("results").elements().forEachRemaining(element -> {
                if (element.get("properties").get("Name").get("title").get(0) == null) return;

                List<ResponsiblePerson> responsiblePersonList = getResponsiblePersonsFromResponse(element, includeNull);
                if (responsiblePersonList.isEmpty() && !includeEmpty) {
                    return;
                }

                Consultant consultant = new Consultant(
                        element.get("properties").get("Name").get("title").get(0).get("plain_text").asText(),
                        UUID.fromString(element.get("id").asText()),
                        responsiblePersonList);

                allConsultants.add(consultant);
            });

            nextCursor = response.get("next_cursor").asText();
            hasMore = response.get("has_more").asBoolean();
        }

        return allConsultants;
    }

    private JsonNode createQueryRequestBody(String nextCursor) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode body = objectMapper.createObjectNode();

        if (nextCursor != null) {
            body.put("start_cursor", nextCursor); // Pass the next cursor for pagination
        }

        return body;
    }
}
