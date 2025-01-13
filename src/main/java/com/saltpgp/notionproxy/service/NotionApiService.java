package com.saltpgp.notionproxy.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.saltpgp.notionproxy.exceptions.NotionException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class NotionApiService {

    private final RestClient restClient;


    public NotionApiService(RestClient.Builder builder) {
        this.restClient = builder.baseUrl("https://api.notion.com/v1").build();
    }

    public JsonNode fetchPage(String pageId, String apiKey, String notionVersion) throws NotionException {
        JsonNode response = restClient
                .get()
                .uri(String.format("/pages/%s", pageId))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .header("Notion-Version", notionVersion)
                .retrieve()
                .body(JsonNode.class);
        if (response == null) {
            throw new NotionException();
        }
        return response;
    }

    public JsonNode fetchDatabase(String database, Object node, String apiKey, String notionVersion) throws NotionException {

        JsonNode response =  restClient
                .post()
                .uri(String.format("/databases/%s/query", database))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .header("Notion-Version", notionVersion)
                .body(node)
                .retrieve()
                .body(JsonNode.class);
        if (response == null) {
            throw new NotionException();
        }
        return response;
    }

}
