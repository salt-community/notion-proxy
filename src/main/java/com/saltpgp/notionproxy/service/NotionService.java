package com.saltpgp.notionproxy.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.saltpgp.notionproxy.exceptions.NotionExceptions;
import com.saltpgp.notionproxy.models.Consultant;
import com.saltpgp.notionproxy.models.ResponsiblePerson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.Collections;
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

    public List<String> getResponsiblePersonNameByUserId(UUID id) throws NotionExceptions {
        JsonNode response = restClient
                .get()
                .uri(String.format("/pages/%s", id))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + API_KEY)
                .header("Notion-Version", NOTION_VERSION)
                .retrieve()
                .body(JsonNode.class);

        if (response == null) {
            throw new NotionExceptions();
        }

        if ((response.get("properties").get("Responsible").get("people").get(0)) == null) {
            return Collections.emptyList();
        }

        List<String> responsibleNames = new ArrayList<>();
        response.get("properties").get("Responsible").get("people").elements().forEachRemaining(element -> {
            responsibleNames.add(element.get("name").asText());
        });

        return responsibleNames;
    }

    public List<Consultant> getConsultants() throws NotionExceptions {
        JsonNode response = restClient
                .post()
                .uri(String.format("/databases/%s/query", DATABASE_ID))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + API_KEY)
                .header("Notion-Version", NOTION_VERSION)
                .retrieve()
                .body(JsonNode.class);

        if (response == null) {
            throw new NotionExceptions();
        }

        List<Consultant> consultants = new ArrayList<>();
        response.get("results").elements().forEachRemaining(element -> {
            if (element.get("properties").get("Name").get("title").get(0) == null) return;

            List<ResponsiblePerson> responsiblePersonList = new ArrayList<>();
            if (element.get("properties").get("Responsible").get("people").get(0) != null) {
                element.get("properties").get("Responsible").get("people").elements().forEachRemaining(element2 -> {
                    ResponsiblePerson responsiblePerson = new ResponsiblePerson(
                            element2.get("name").asText(),
                            UUID.fromString(element2.get("id").asText()));

                    responsiblePersonList.add(responsiblePerson);
                });
            }

            Consultant consultant = new Consultant(
                    element.get("properties").get("Name").get("title").get(0).get("plain_text").asText(),
                    UUID.fromString(element.get("id").asText()),
                    responsiblePersonList);

            consultants.add(consultant);
        });

        return consultants;
    }
}
