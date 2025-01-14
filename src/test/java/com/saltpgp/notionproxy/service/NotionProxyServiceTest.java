package com.saltpgp.notionproxy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saltpgp.notionproxy.exceptions.NotionException;
import com.saltpgp.notionproxy.exceptions.NotionNotFoundException;
import com.saltpgp.notionproxy.models.Consultant;
import com.saltpgp.notionproxy.models.Developer;
import com.saltpgp.notionproxy.models.ResponsiblePerson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@TestPropertySource(properties = {

})
class NotionProxyServiceTest {

    NotionApiService mockApiService;

    NotionService notionService;

    ObjectMapper mapper;

    private String DATABASE_ID;

    private String CORE_DATABASE_ID;

    private String SCORE_DATABASE_ID;

    private String databaseResponse;
    private String coreDatabaseResponse;
    private String scoreDatabaseResponse;
    private String consultantPageResponse;


    @BeforeEach
    void setUp() throws JsonProcessingException, NotionException {

        mockApiService = mock(NotionApiService.class);

        notionService = new NotionService(mockApiService);

        mapper = new ObjectMapper();

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

        coreDatabaseResponse = """
                {
                    "object": "list",
                    "results": [
                        {
                            "object": "page",
                            "id": "33333333-3333-3333-3333-333333333333",
                            "properties": {
                                "Person": {
                                    "id": "person_id",
                                    "type": "people",
                                    "people": [
                                        {
                                            "object": "user",
                                            "id": "44444444-4444-4444-4444-444444444444",
                                            "name": "Responsible Person 1",
                                            "person": {
                                                "email": "responsibleperson1@appliedtechnology.se"
                                            }
                                        }
                                    ]
                                }
                            }
                        },
                        {
                            "object": "page",
                            "id": "55555555-5555-5555-5555-555555555555",
                            "properties": {
                                "Person": {
                                    "id": "person_id",
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
        scoreDatabaseResponse = """
                                {
                                "object": "list",
                                "results": [
                        {
                            "object": "page",
                             "id": "11111111-1111-1111-1111-111111111111",
                                "properties": {
                            "Categories": {
                                "multi_select": [
                                {
                                    "name": "backend"
                                },
                                {
                                    "name": "java"
                                }
                          ]
                            },
                            "Score": {
                                "number": 100
                            },
                            "Name": {
                                "title": [
                                {
                                    "plain_text": "Three Small Methods"
                                }
                          ]
                            }
                        }
                        },
                        {
                            "object": "page",
                                "properties": {
                            "Categories": {
                                "multi_select": [
                                {
                                    "name": "frontend"
                                },
                                {
                                    "name": "javascript"
                                }
                          ]
                            },
                            "Score": {
                                "number": 95
                            },
                            "Name": {
                                "title": [
                                {
                                    "plain_text": "UI Enhancement"
                                }
                          ]
                            }
                        }
                        }
                  ],
                        "next_cursor": null,
                                "has_more": false
                }""";
         consultantPageResponse = """
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
                                    "email": "saltie1@example.com"
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
                                        }
                                    ]
                                }
                            }
                        }
                """;

        when(mockApiService.fetchDatabase(CORE_DATABASE_ID,NotionServiceFilters.FILTER_RESPONSIBLE_PEOPLE)).thenReturn(mapper.readTree(coreDatabaseResponse));

        when(mockApiService.fetchDatabase(DATABASE_ID, NotionServiceFilters.getFilterOnAssignment(null))).thenReturn(mapper.readTree(databaseResponse));

        when(mockApiService.fetchDatabase(DATABASE_ID, null)).thenReturn(mapper.readTree(databaseResponse));

    }

    @Test
    void shouldFindAllConsultantsWithIncludeEmptyTrue() throws NotionException, JsonProcessingException {

        List<Consultant> consultants = notionService.getAllConsultants( true);
        assertEquals(2, consultants.size());
        assertEquals("Test Saltie 1", consultants.get(0).name());
        assertEquals("Test Saltie 2", consultants.get(1).name());
    }

    @Test
    void shouldFindAllDevelopers() throws NotionException {

        List<Developer> developers = notionService.getAllDevelopers();

        assertEquals(2, developers.size());
        assertEquals("Test Saltie 1", developers.get(0).getName());
        assertEquals("https://github.com/saltie2", developers.get(1).getGithubUrl());
    }

    @Test
    void getAllResponsiblePeopleTests() throws NotionException {

        List<ResponsiblePerson> responsiblePeople = notionService.getAllResponsiblePeople( false);
//        Assert
        assertEquals(2, responsiblePeople.size());
        assertEquals("Responsible Person 1", responsiblePeople.get(0).name());
        assertEquals("Responsible Person 2", responsiblePeople.get(1).name());
        assertEquals("responsibleperson1@appliedtechnology.se", responsiblePeople.get(0).email());
        assertEquals("responsibleperson2@appliedtechnology.se", responsiblePeople.get(1).email());
    }

    @Test
    void shouldFindDeveloperByIdWithScore() throws NotionException, NotionNotFoundException {
        //Given
        String id = "11111111-1111-1111-1111-111111111111";

        //When

        //Then
        Developer developer = notionService.getDeveloperByIdWithScore(UUID.fromString(id));

        assertEquals(100, developer.getScores().get(0).getScore());
        assertEquals("Three Small Methods", developer.getScores().get(0).getName());
        assertEquals(95, developer.getScores().get(1).getScore());
        assertEquals("UI Enhancement", developer.getScores().get(1).getName());
        assertEquals("https://github.com/saltie1", developer.getGithubUrl());
    }

    @Test
    void shouldGetConsultantById() throws NotionException {
        // Given
        UUID consultantId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        // When

        // Then
        Consultant consultant = notionService.getConsultantById(consultantId);

        assertNotNull(consultant);
        assertEquals("Test Saltie 1", consultant.name());
        assertEquals(consultantId, consultant.uuid());

        // may want to assert responsiblePersonList content if applicable
        assertNotNull(consultant.responsiblePersonList());
        assertEquals(1, consultant.responsiblePersonList().size());
        assertEquals("Responsible Person 1", consultant.responsiblePersonList().getFirst().name());
    }
    


}