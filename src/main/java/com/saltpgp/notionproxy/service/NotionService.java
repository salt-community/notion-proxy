package com.saltpgp.notionproxy.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saltpgp.notionproxy.dtos.ConsultantDto;
import com.saltpgp.notionproxy.models.Consultant;
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
        restClient = RestClient.builder()
                .baseUrl("https://api.notion.com/v1")
                .build();
    }

    public String getResponsiblePersonNameByUserId(UUID id) {
        JsonNode response = restClient.get()
                .uri(String.format("/pages/%s", id))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + API_KEY)
                .header("Notion-Version", NOTION_VERSION)
                .retrieve()
                .body(JsonNode.class);


        System.out.println("response = " + response);
        System.out.println("response.get(\"properties\").get(\"Responsible\").get(\"people\").get(0).get(\"name\") = " + response.get("properties").get("Responsible").get("people").get(0).get("name"));
        return "hi";
    }

    public String getResponsiblePerson() {
        try {
            String response = restClient.post()
                    .uri(String.format("/databases/%s/query", DATABASE_ID))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Notion-Version", NOTION_VERSION)
                    .body("{\n" +
                            "  \"filter\": {\n" +
                            "    \"property\": \"Name\",\n" +
                            "    \"title\": {\n" +
                            "      \"equals\": \"John Doe\"\n" +
                            "    }\n" +
                            "  }\n" +
                            "}")
                    .retrieve()
                    .body(String.class);

            System.out.println("response = " + response);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return "hello";
    }

    public List<Consultant> getConsultants() {
        JsonNode response = restClient.post()
                .uri(String.format("/databases/%s/query", DATABASE_ID))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + API_KEY)
                .header("Notion-Version", NOTION_VERSION)
                .retrieve()
                .body(JsonNode.class);
        List<Consultant> consultants = new ArrayList<>();
                response.get("results").elements().forEachRemaining(element -> {
           if(element.get("properties").get("Name").get("title").get(0) == null) return;
           Consultant consultant = new Consultant(element.get("properties").get("Name").get("title").get(0).get("plain_text").asText(),UUID.fromString(element.get("id").asText()));
           consultants.add(consultant);
        });
        System.out.println(consultants);

        return consultants;
    }
}
