package com.saltpgp.notionproxy.modules.idcard.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saltpgp.notionproxy.api.bucket.BucketApiService;
import com.saltpgp.notionproxy.api.notion.NotionApiService;
import com.saltpgp.notionproxy.api.notion.filter.NotionServiceFilters;
import com.saltpgp.notionproxy.exceptions.NotionException;
import com.saltpgp.notionproxy.exceptions.NotionNotFoundException;
import org.junit.jupiter.api.BeforeEach;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class IdCardServiceTest {

    NotionApiService mockApiService;
    BucketApiService mockBucketApiService;

    IdCardService idCardService;

    ObjectMapper mapper;

    private final String DATABASE_ID = "DATABASE_ID";
    public static final String FILTER_EMAIL = """
                "filter": {
                    "property": "Email",
                    "email": {
                        "equals": "%s"
                    }
                }
            """;
    @BeforeEach
    void setUp() throws JsonProcessingException, NotionException, NotionNotFoundException {
        String databaseResponse;

        mockApiService = mock(NotionApiService.class);
        mockBucketApiService = mock(BucketApiService.class);

        idCardService = new IdCardService(mockApiService, mockBucketApiService, DATABASE_ID);

        mapper = new ObjectMapper();

        String pageResponse = """
                    {
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
                            "Email": {
                                "id": "private_email",
                                "type": "email",
                                "email": "saltie@example.com"
                            },
                            "Course": {
                                "id": "course",
                                "type": "select",
                                "select": {
                                    "name": "jfs-sthlm-2024-09-06",
                                }
                            },
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
                                "Email": {
                                    "id": "private_email",
                                    "type": "email",
                                    "email": "saltie@example.com"
                                },
                                "Course": {
                                    "id": "course",
                                    "type": "select",
                                    "select": {
                                        "name": "jfs-sthlm-2024-09-06",
                                    }
                                },
                            }
                        },
                    ],
                    "next_cursor": null,
                    "has_more": false
                }
                """;

        when(mockApiService.fetchDatabase(DATABASE_ID, FILTER_EMAIL)).thenReturn(mapper.readTree(databaseResponse));
        when(mockApiService.fetchPage("11111111-1111-1111-1111-111111111111")).thenReturn(mapper.readTree(pageResponse));
    }

}