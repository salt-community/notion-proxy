package com.saltpgp.notionproxy.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.saltpgp.notionproxy.exceptions.NotionException;
import com.saltpgp.notionproxy.exceptions.NotionNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Service
public class NotionApiService {

    private final RestClient restClient;
    private final String API_KEY;
    private final String NOTION_VERSION;

    public NotionApiService(RestClient.Builder builder,
                            @Value("${NOTION_API_KEY}") String API_KEY,
                            @Value("${NOTION_VERSION}") String NOTION_VERSION) {
        this.restClient = builder.baseUrl("https://api.notion.com/v1").build();
        this.API_KEY = API_KEY;
        this.NOTION_VERSION = NOTION_VERSION;
    }

    public JsonNode fetchPage(String pageId) throws NotionException {
        String uri = String.format("/pages/%s", pageId);
        return executeRequest(() -> restClient
                .get()
                .uri(uri)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + API_KEY)
                .header("Notion-Version", NOTION_VERSION)
                .retrieve()
                .body(JsonNode.class), pageId, "page");
    }

    public JsonNode fetchDatabase(String database, Object node) throws NotionException {
        String uri = String.format("/databases/%s/query", database);
        return executeRequest(() -> restClient
                .post()
                .uri(uri)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + API_KEY)
                .header("Notion-Version", NOTION_VERSION)
                .body(node)
                .retrieve()
                .body(JsonNode.class), database, "database");
    }

    private JsonNode executeRequest(RequestExecutor executor, String id, String type) throws NotionException {
        try {
            return executor.execute();
        } catch (HttpClientErrorException e) {
            switch (e.getStatusText()) {
                case "Not Found":
                    if ("page".equals(type)) {
                        throw new NotionException("Page ID didn't exist in Notion: " + id);
                    } else if ("database".equals(type)) {
                        throw new NotionException("Database didn't exist in Notion");
                    }
                    break;
                case "Unauthorized":
                    throw new NotionException("Unauthorized to access Notion API. Check the API key.");
                case "Bad Request":
                    throw new NotionException("Bad request to the Notion API. Check the API request.");
                default:
                    throw new NotionException("Unknown error occurred with HTTP status: " + e.getStatusText());
            }
        } catch (ResourceAccessException e) {
            throw new NotionException("Can't access Notion API. Check if the Notion proxy can send requests.");
        } catch (Exception e) {
            throw new NotionException("Unknown error occurred while trying to send request to Notion.");
        }
        return null;
    }

    @FunctionalInterface
    private interface RequestExecutor {
        JsonNode execute() throws Exception;
    }
}
