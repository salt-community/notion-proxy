package com.saltpgp.notionproxy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saltpgp.notionproxy.exceptions.NotionException;
import com.saltpgp.notionproxy.models.Consultant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;


@RestClientTest(NotionService.class)
class NotionProxyServiceTest {

    @Autowired
    MockRestServiceServer server;

    @Autowired
    NotionService notionService;

    @Value("${DATABASE_ID}")
    private String DATABASE_ID;

    @Autowired
    ObjectMapper objectMapper;


    @Test
    void shouldFindAllConsultantswithIncludeEmptyTrue() throws NotionException, JsonProcessingException {

        String jsonResponse = """
                {
                    "object": "list",
                    "results": [
                        {
                            "object": "page",
                            "id": "55fa77df-612a-4644-a058-45b88934366a", 
                            "properties": {
                                "Name": {
                                    "id": "title",
                                    "type": "title",
                                    "title": [
                                        {
                                            "plain_text": "Test Consultant1",
                                            "href": null
                                        }
                                    ]
                                },
                                "Responsible": {
                                    "id": "55fa77df-612a-4644-a058-45b88934366a",
                                    "type": "people",
                                    "people": [
                                        {
                                            "object": "user",
                                            "id": "55fa77df-612a-4644-a058-45b88934366a",
                                            "name": "Test Responsible1",
                                            "person": {
                                                "email": "test1@appliedtechnology.se"
                                            }
                                        }
                                    ]
                                }
                            }
                        },
                		{
                            "object": "page",
                            "id": "55fa77df-612a-4644-a058-45b88934366a",
                            "properties": {
                                "Name": {
                                    "id": "title",
                                    "type": "title",
                                    "title": [
                                        {
                                            "plain_text": "Test Consultant2",
                                            "href": null
                                        }
                                    ]
                                },
                                "Responsible": {
                                    "id": "55fa77df-612a-4644-a058-45b88934366a",
                                    "type": "people",
                                    "people": [
                                        {
                                            "object": "user",
                                            "id": "55fa77df-612a-4644-a058-45b88934366a",
                                            "name": "Test Responsible2",
                                            "person": {
                                                "email": "test2@appliedtechnology.se"
                                            }
                                        }
                                    ]
                                }
                            }
                        }
                    ],
                    "next_cursor": "55fa77df-612a-4644-a058-45b88934366a",
                    "has_more": false
                }
                """;


        JsonNode jsonNodeResponse = objectMapper.readTree(jsonResponse);

        server.expect(requestTo(String.format("https://api.notion.com/v1/databases/%s/query", DATABASE_ID))) // Set expectation for request
                .andRespond(withSuccess(jsonNodeResponse.toString(), MediaType.APPLICATION_JSON)); // Mock response

        List<Consultant> consultants = notionService.getAllConsultants(true, false);

        assertEquals(2, consultants.size());
        assertEquals("Test Consultant1", consultants.get(0).name());
        assertEquals("Test Consultant2", consultants.get(1).name());
    }

}