package com.saltpgp.notionproxy.developer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saltpgp.notionproxy.api.bucket.BucketApiService;
import com.saltpgp.notionproxy.modules.developer.model.Developer;
import com.saltpgp.notionproxy.exceptions.InvalidFilterException;
import com.saltpgp.notionproxy.exceptions.NotionException;
import com.saltpgp.notionproxy.exceptions.NotionNotFoundException;
import com.saltpgp.notionproxy.api.notion.NotionApiService;
import com.saltpgp.notionproxy.api.notion.filter.NotionServiceFilters;
import com.saltpgp.notionproxy.modules.developer.service.DeveloperService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class DeveloperServiceTest {

    NotionApiService mockApiService;
    BucketApiService mockBucketApiService;

    DeveloperService developerService;

    ObjectMapper mapper;

    private final String DATABASE_ID = "DATABASE_ID";

    @BeforeEach
    void setUp() throws JsonProcessingException, NotionException, NotionNotFoundException {
        String databaseResponse;

        mockApiService = mock(NotionApiService.class);
        mockBucketApiService = mock(BucketApiService.class);

        developerService = new DeveloperService(mockApiService, mockBucketApiService,DATABASE_ID);

        mapper = new ObjectMapper();


            String pageResponse = """
        {
            "id": "11111111-1111-1111-1111-111111111111",
            "properties": {
                "Name": {
                    "title": [
                        {
                            "plain_text": "Test Saltie 1"
                        }
                    ]
                },
                "GitHub": {
                    "url": "https://github.com/saltie1"
                },
                "Private Email": {
                    "email": "saltie@example.com"
                },
                "Responsible": {
                    "people": [
                        {
                            "name": "Responsible Person 1",
                            "person": {
                                "email": "responsibleperson1@appliedtechnology.se"
                            }
                        }
                    ]
                }
            }
        }
    """;

        databaseResponse = """
                {
                    "object": "list",
                    "results": [
                        {
                            "object": "page",
                            "id": "11111111-1111-1111-1111-111111111111",
                            "properties": {
                                "Name": {
                                    "id": "title",
                                    "type": "title",
                                    "title": [
                                        {
                                            "plain_text": "Test Saltie 1",
                                            "href": null
                                        }
                                    ]
                                },
                                "GitHub": {
                                    "id": "github",
                                    "type": "url",
                                    "url": "https://github.com/saltie1"
                                },
                                "Private Email": {
                                    "id": "private_email",
                                    "type": "email",
                                    "email": "saltie@example.com"
                                },
                                "Responsible": {
                                    "id": "33333333-3333-3333-3333-333333333333",
                                    "type": "people",
                                    "people": [
                                        {
                                            "object": "user",
                                            "id": "44444444-4444-4444-4444-444444444444",
                                            "name": "Responsible Person 1",
                                            "person": {
                                                "email": "responsibleperson1@appliedtechnology.se"
                                            }
                                        },
                                        {
                                            "object": "user",
                                            "id": "44444444-4444-4444-4444-444444444445",
                                            "name": "Sales Person 1",
                                            "person": {
                                                "email": "salesperson1@appliedtechnology.se"
                                            }
                                        }
                                    ]
                                }
                            }
                        },
                		{
                            "object": "page",
                            "id": "22222222-2222-2222-2222-222222222222",
                            "properties": {
                                "Name": {
                                    "id": "title",
                                    "type": "title",
                                    "title": [
                                        {
                                            "plain_text": "Test Saltie 2",
                                            "href": null
                                        }
                                    ]
                                },
                                "GitHub": {
                                    "id": "github",
                                    "type": "url",
                                    "url": "https://github.com/saltie2"
                                },
                                "Private Email": {
                                    "id": "private_email",
                                    "type": "email",
                                    "email": "saltie2@example.com"
                                },
                                "Responsible": {
                                    "id": "55555555-5555-5555-5555-555555555555",
                                    "type": "people",
                                    "people": [
                                        {
                                            "object": "user",
                                            "id": "66666666-6666-6666-6666-666666666666",
                                            "name": "Responsible Person 2",
                                            "person": {
                                                "email": "responsibleperson2@appliedtechnology.se"
                                            }
                                        }
                                    ]
                                }
                            }
                        }
                    ],
                    "next_cursor": null,
                    "has_more": false
                }
                """;

        when(mockApiService.fetchDatabase(DATABASE_ID, NotionServiceFilters.getFilterOnAssignment(null))).thenReturn(mapper.readTree(databaseResponse));
        when(mockApiService.fetchPage("11111111-1111-1111-1111-111111111111")).thenReturn(mapper.readTree(pageResponse));
        when(mockApiService.fetchDatabase(eq(DATABASE_ID), anyString())).thenReturn(mapper.readTree(databaseResponse));


    }

    @Test
    void shouldGetDeveloperById() throws NotionException, NotionNotFoundException {

        UUID developerId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        Developer developer = developerService.getDeveloperById(developerId,false);

        assertNotNull(developer);
        assertEquals("Test Saltie 1", developer.getName());
        assertEquals(developerId, developer.getId());

        assertEquals("https://github.com/saltie1", developer.getGithubUrl());
        assertEquals("saltie@example.com", developer.getEmail());
    }

    @Test
    void shouldGetAllDevelopers() throws NotionException, NotionNotFoundException {
        List<Developer> developers = developerService.getAllDevelopers(null,false);

        assertNotNull(developers);
        assertEquals(2, developers.size());

        Developer developer = developers.getFirst();
        assertEquals("Test Saltie 1", developer.getName());
        assertEquals(UUID.fromString("11111111-1111-1111-1111-111111111111"), developer.getId());
        assertEquals("https://github.com/saltie1", developer.getGithubUrl());
        assertEquals("saltie@example.com", developer.getEmail());
    }
    @Test
    void shouldThrowExceptionForInvalidFilter() {
        String invalidFilter = "INVALID_STATUS";

        InvalidFilterException exception = assertThrows(InvalidFilterException.class, () -> {
            developerService.getAllDevelopers(invalidFilter,false);
        });

        assertEquals("Invalid filter value: " + invalidFilter, exception.getMessage());
    }
}