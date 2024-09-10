package com.saltpgp.notionproxy.service;

import com.fasterxml.jackson.databind.JsonNode;
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

        List<ResponsiblePerson> responsiblePersonList = getResponsiblePersonsFromResponse(response);

        return new Consultant(
                response.get("properties").get("Name").get("title").get(0).get("plain_text").asText(),
                UUID.fromString(response.get("id").asText()),
                responsiblePersonList);
    }

    private List<ResponsiblePerson> getResponsiblePersonsFromResponse(JsonNode response) {
        List<ResponsiblePerson> responsiblePersonList = new ArrayList<>();
        if (response.get("properties").get("Responsible").get("people").get(0) != null) {
            response.get("properties").get("Responsible").get("people").elements().forEachRemaining(element2 -> {
                ResponsiblePerson responsiblePerson = new ResponsiblePerson(
                        element2.get("name").asText(),
                        UUID.fromString(element2.get("id").asText()));
                responsiblePersonList.add(responsiblePerson);
            });
        }
        return responsiblePersonList;
    }

    public List<Consultant> getConsultants() throws NotionException {
        JsonNode response = restClient
                .post()
                .uri(String.format("/databases/%s/query", DATABASE_ID))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + API_KEY)
                .header("Notion-Version", NOTION_VERSION)
                .retrieve()
                .body(JsonNode.class);

        if (response == null) {
            throw new NotionException();
        }

        List<Consultant> consultants = new ArrayList<>();
        response.get("results").elements().forEachRemaining(element -> {
            if (element.get("properties").get("Name").get("title").get(0) == null) return;

            List<ResponsiblePerson> responsiblePersonList = getResponsiblePersonsFromResponse(response);

            Consultant consultant = new Consultant(
                    element.get("properties").get("Name").get("title").get(0).get("plain_text").asText(),
                    UUID.fromString(element.get("id").asText()),
                    responsiblePersonList);

            consultants.add(consultant);
        });

        return consultants;
    }
}
