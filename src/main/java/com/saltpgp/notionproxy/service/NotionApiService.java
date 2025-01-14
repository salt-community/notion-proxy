package com.saltpgp.notionproxy.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.saltpgp.notionproxy.exceptions.NotionException;
import org.apache.logging.log4j.message.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class NotionApiService {

    private final RestClient restClient;

    @Value("${NOTION_API_KEY}")
    private String API_KEY;
    @Value("${NOTION_VERSION}")
    private String NOTION_VERSION;

    public NotionApiService(RestClient.Builder builder) {
        this.restClient = builder.baseUrl("https://api.notion.com/v1").build();
    }

    public JsonNode fetchPage(String pageId) throws NotionException {
        JsonNode response = restClient
                .get()
                .uri(String.format("/pages/%s", pageId))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + API_KEY)
                .header("Notion-Version", NOTION_VERSION)
                .retrieve()
                .body(JsonNode.class);
        if (response == null) {
            throw new NotionException("Id didn't exist in the pages");
        }
        return response;
    }

    public JsonNode fetchDatabase(String database, Object node) throws NotionException {

        JsonNode response =  restClient
                .post()
                .uri(String.format("/databases/%s/query", database))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + API_KEY)
                .header("Notion-Version", NOTION_VERSION)
                .body(node)
                .retrieve()
                .body(JsonNode.class);
        if (response == null) {
            throw new NotionException();
        }
        return response;
    }

}
