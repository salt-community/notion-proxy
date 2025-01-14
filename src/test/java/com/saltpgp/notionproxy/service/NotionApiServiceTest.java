package com.saltpgp.notionproxy.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.saltpgp.notionproxy.exceptions.NotionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.HttpMethod;

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
    void testFetchPage_Success() throws NotionException {
        // Sample mock response from Notion API
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
    void testFetchPage_NotFound() {
        String pageId = "12345";

        server.expect(requestTo("https://api.notion.com/v1/pages/12345"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer " + API_KEY))
                .andExpect(header("Notion-Version", NOTION_VERSION))
                .andRespond(withStatus(HttpStatus.ACCEPTED));

        NotionException exception = assertThrows(NotionException.class, () -> notionApiService.fetchPage(pageId));

        assertEquals("Pages id didn't exist in notion", exception.getMessage());
    }
}
