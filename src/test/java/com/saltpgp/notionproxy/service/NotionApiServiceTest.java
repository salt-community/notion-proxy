package com.saltpgp.notionproxy.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.saltpgp.notionproxy.exceptions.NotionException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.ResourceAccessException;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest(NotionApiService.class)
class NotionApiServiceTest {

    @Autowired
    MockRestServiceServer server;

    @Autowired
    NotionApiService notionApiService;

    @Value("${NOTION_API_KEY}")
    private String API_KEY;
    @Value("${NOTION_VERSION}")
    private String NOTION_VERSION;

    @Test
    void fetchPage_Success() throws NotionException {
        String pageId = "12345";
        String mockResponse = "{\"id\":\"12345\", \"object\":\"page\", \"properties\":{}}";

        server.expect(requestTo("https://api.notion.com/v1/pages/12345"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer " + API_KEY))
                .andExpect(header("Notion-Version", NOTION_VERSION))
                .andRespond(withSuccess(mockResponse, org.springframework.http.MediaType.APPLICATION_JSON));

        JsonNode response = notionApiService.fetchPage(pageId);

        assertNotNull(response);
        assertEquals("12345", response.get("id").asText());
    }

    @Test
    void fetchPage_NotFound() {
        String pageId = "12345";

        server.expect(requestTo("https://api.notion.com/v1/pages/12345"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer " + API_KEY))
                .andExpect(header("Notion-Version", NOTION_VERSION))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        NotionException exception = assertThrows(NotionException.class, () -> notionApiService.fetchPage(pageId));

        assertEquals("Page ID didn't exist in Notion: " + pageId, exception.getMessage());
    }

    @Test
    void fetchPage_Unauthorized() {
        String pageId = "12345";

        server.expect(requestTo("https://api.notion.com/v1/pages/12345"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer " + API_KEY))
                .andExpect(header("Notion-Version", NOTION_VERSION))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED));

        NotionException exception = assertThrows(NotionException.class, () -> notionApiService.fetchPage(pageId));

        assertEquals("Unauthorized to access Notion API. Check the API key.", exception.getMessage());
    }

    @Test
    void fetchPage_BadRequest() {
        String pageId = "12345";

        server.expect(requestTo("https://api.notion.com/v1/pages/12345"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer " + API_KEY))
                .andExpect(header("Notion-Version", NOTION_VERSION))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        NotionException exception = assertThrows(NotionException.class, () -> notionApiService.fetchPage(pageId));

        assertEquals("Bad request to the Notion API. Check the API request.", exception.getMessage());
    }

    @Test
    void fetchPage_ResourceAccessException() {
        String pageId = "12345";

        server.expect(requestTo("https://api.notion.com/v1/pages/12345"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer " + API_KEY))
                .andExpect(header("Notion-Version", NOTION_VERSION))
                .andRespond(request -> {
                    throw new ResourceAccessException("");
                });;

        NotionException exception = assertThrows(NotionException.class, () -> notionApiService.fetchPage(pageId));

        assertEquals("Can't access Notion API. Check if the Notion proxy can send requests.", exception.getMessage());
    }

    @Test
    void fetchDatabase_Success() throws NotionException {
        String databaseId = "abc123";
        String mockDatabaseResponse = "{\"results\":[]}";

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode body = objectMapper.createObjectNode();

        server.expect(requestTo("https://api.notion.com/v1/databases/abc123/query"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "Bearer " + API_KEY))
                .andExpect(header("Notion-Version", NOTION_VERSION))
                .andExpect(content().json("{ }"))
                .andRespond(withSuccess(mockDatabaseResponse, org.springframework.http.MediaType.APPLICATION_JSON));

        JsonNode response = notionApiService.fetchDatabase(databaseId, body);

        assertNotNull(response);
        assertEquals("[]", response.get("results").toString());
    }

    @Test
    void fetchDatabase_NotFound() {
        String databaseId = "abc123";

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode body = objectMapper.createObjectNode();

        server.expect(requestTo("https://api.notion.com/v1/databases/abc123/query"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "Bearer " + API_KEY))
                .andExpect(header("Notion-Version", NOTION_VERSION))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        NotionException exception = assertThrows(NotionException.class, () -> notionApiService.fetchDatabase(databaseId, body));

        assertEquals("Database didn't exist in Notion", exception.getMessage());
    }
}
