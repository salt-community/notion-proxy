package com.saltpgp.notionproxy.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

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
        String response = restClient.get()
                .uri(String.format("/pages/%s", id))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + API_KEY)
                .header("Notion-Version", NOTION_VERSION)
                .retrieve()
                .body(String.class);

        System.out.println("response = " + response);
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
}
