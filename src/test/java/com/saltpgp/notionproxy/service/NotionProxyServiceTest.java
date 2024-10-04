package com.saltpgp.notionproxy.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saltpgp.notionproxy.exceptions.NotionException;
import com.saltpgp.notionproxy.models.Consultant;
import com.saltpgp.notionproxy.models.Developer;
import com.saltpgp.notionproxy.models.ResponsiblePerson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;


@RestClientTest(NotionService.class)
class NotionProxyServiceTest {

    @Autowired
    MockRestServiceServer server;

    @Autowired
    NotionService notionService;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${DATABASE_ID}")
    private String DATABASE_ID;

    @Value("${CORE_DATABASE_ID}")
    private String CORE_DATABASE_ID;

    @Value("${SCORE_DATABASE_ID}")
    private String SCORE_DATABASE_ID;

    @Test
    void shouldFindAllConsultantswithIncludeEmptyTrue() throws NotionException, JsonProcessingException {

        String jsonResponse = """
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
                                            "plain_text": "Test Consultant1",
                                            "href": null
                                        }
                                    ]
                                },
                                "Responsible": {
                                    "id": "11111111-1000-1111-1111-111111111111",
                                    "type": "people",
                                    "people": [
                                        {
                                            "object": "user",
                                            "id": "11111111-1111-1111-2222-111111111111",
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
                            "id": "11111111-1111-1111-1111-111111111112",
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
                                    "id": "11111111-1000-1111-1111-111111111111",
                                    "type": "people",
                                    "people": [
                                        {
                                            "object": "user",
                                            "id": "11111111-1000-1111-2222-111111111112",
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


        server.expect(requestTo(String.format("https://api.notion.com/v1/databases/%s/query", DATABASE_ID))) // Set expectation for request
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON)); // Mock response

        List<Consultant> consultants = notionService.getAllConsultants(true, false);

        assertEquals(2, consultants.size());
        assertEquals("Test Consultant1", consultants.get(0).name());
        assertEquals("Test Consultant2", consultants.get(1).name());
    }

    @Test
    void shouldFindAllDevelopers() throws NotionException {
        //given
        String jsonResponse = """
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
                                            "plain_text": "Desarrollador A",
                                            "href": null
                                        }
                                    ]
                                },
                                "GitHub": {
                                    "id": "github",
                                    "type": "url",
                                    "url": "https://github.com/userA"
                                },
                                "Private Email": {
                                    "id": "private_email",
                                    "type": "email",
                                    "email": "userA@example.com"
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
                                            "plain_text": "Desarrollador B",
                                            "href": null
                                        }
                                    ]
                                },
                                "GitHub": {
                                    "id": "github",
                                    "type": "url",
                                    "url": "https://github.com/userB"
                                },
                                "Private Email": {
                                    "id": "private_email",
                                    "type": "email",
                                    "email": "userB@example.com"
                                }
                            }
                        }
                    ],
                    "next_cursor": null,
                    "has_more": false
                }
                """;

        //when
        server.expect(requestTo(String.format("https://api.notion.com/v1/databases/%s/query", DATABASE_ID)))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        //then
        List<Developer> developers = notionService.getAllDevelopers();

        assertEquals(2, developers.size());
        assertEquals("Desarrollador A", developers.get(0).getName());
        assertEquals("https://github.com/userB", developers.get(1).getGithubUrl());


    }

    @Test
    void getAllResponsiblePeopleTests() throws NotionException {

//        Arrange
        String jsonResponse = """
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
                                            "email": "person1@appliedtechnology.se"
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
                                            "email": "person2@appliedtechnology.se"
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

//        Act
        server.expect(requestTo(String.format("https://api.notion.com/v1/databases/%s/query", CORE_DATABASE_ID)))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        List<ResponsiblePerson> responsiblePeople = notionService.getAllResponsiblePeople(false, false);

//        Assert
        assertEquals(2, responsiblePeople.size());
        assertEquals("Responsible Person 1", responsiblePeople.get(0).name());
        assertEquals("Responsible Person 2", responsiblePeople.get(1).name());
        assertEquals("person1@appliedtechnology.se", responsiblePeople.get(0).email());
        assertEquals("person2@appliedtechnology.se", responsiblePeople.get(1).email());
    }

    @Test
    void shouldFindDeveloperByIdWithScore() throws NotionException {
        //Given
        String id= "11111111-1111-1111-1111-111111111111";
        String jsonResponse = """
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

        String jsonResponse2 = """
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
                                        "plain_text": "Desarrollador A",
                                        "href": null
                                    }
                                ]
                            },
                            "GitHub": {
                                "id": "github",
                                "type": "url",
                                "url": "https://github.com/userA"
                            },
                            "Private Email": {
                                "id": "private_email",
                                "type": "email",
                                "email": "userA@example.com"
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
                                        "plain_text": "Desarrollador B",
                                        "href": null
                                    }
                                ]
                            },
                            "GitHub": {
                                "id": "github",
                                "type": "url",
                                "url": "https://github.com/userB"
                            },
                            "Private Email": {
                                "id": "private_email",
                                "type": "email",
                                "email": "userB@example.com"
                            }
                        }
                    }
                ],
                "next_cursor": null,
                "has_more": false
            }
            """;
        //When
        server.expect(requestTo(String.format("https://api.notion.com/v1/databases/%s/query", SCORE_DATABASE_ID)))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        server.expect(requestTo(String.format("https://api.notion.com/v1/databases/%s/query", DATABASE_ID)))
                .andRespond(withSuccess(jsonResponse2, MediaType.APPLICATION_JSON));
        //Then
        Developer developer = notionService.getDeveloperByIdWithScore(UUID.fromString(id));

        assertEquals(100, developer.getScores().get(0).getScore());
        assertEquals("Three Small Methods", developer.getScores().get(0).getName());
        assertEquals(95, developer.getScores().get(1).getScore());
        assertEquals("UI Enhancement", developer.getScores().get(1).getName());
        assertEquals("https://github.com/userA", developer.getGithubUrl());
    }

    @Test
    void shouldGetConsultantById() throws NotionException {
        // Given
        UUID consultantId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        String jsonResponse = """
            {
                       "object": "page",
                       "id": "11111111-1111-1111-1111-111111111111",
                       "properties": {
                         "Name": {
                           "id": "title",
                           "type": "title",
                           "title": [
                             {
                               "plain_text": "Adam West",
                               "href": null
                             }
                           ]
                         },
                         "Responsible": {
                           "id": "responsible_persons",
                           "type": "multi_select",
                   "people": [
           {
                 "name": "Anne Mary",
                 "id": "22222222-2222-2222-2222-222222222222",
                 "email": "anna@test.se"
               }
           ],
                           "multi_select": [
                             {
                               "id": "22222222-2222-2222-2222-222222222222",
                               "name": "Anne Mary",
                               "color": "default"
                             }
                           ]
                         },
                         "Responsible Persons": {
                           "id": "responsible_persons",
                           "type": "multi_select",
                           "multi_select": [
                             {
                               "id": "22222222-2222-2222-2222-222222222222",
                               "name": "Anne Mary",
                               "color": "default"
                             }
                           ]
                         }
                       }
                     }
        """;

        String databaseResponse = """
                {
                  "object": "list",
                  "results": [
                    {
                      "object": "page",
                      "id": "33333333-3333-3333-3333-333333333333",
                      "created_time": "2024-09-23T07:41:00.000Z",
                      "last_edited_time": "2024-09-26T11:40:00.000Z",
                      "created_by": {
                        "object": "user",
                        "id": "e62df52d-08bf-4a76-a0c4-7cff3cef7adf"
                      },
                      "last_edited_by": {
                        "object": "user",
                        "id": "44444444-4444-4444-4444-444444444444"
                      },
                      "cover": null,
                      "icon": null,
                      "parent": {
                        "type": "database_id",
                        "database_id": "44444444-4444-4444-4444-444444444444"
                      },
                      "archived": false,
                      "in_trash": false,
                      "properties": {
                        "Person": {
                  "people" : [
              {
        "name": "Anne Mary",
                 "id": "22222222-2222-2222-2222-222222222222",
                 "email": "anna@test.se",
      "person": {
                 "name": "Anne Mary",
                 "id": "22222222-2222-2222-2222-222222222222",
                 "email": "anna@test.se"
               }
      }
          ]
                },
                        "Company": {
                          "id": "%3B%3EoG",
                          "type": "rollup",
                          "rollup": {
                            "type": "array",
                            "array": [],
                            "function": "show_original"
                          }
                        }
                        }
                        }
        ]}
      """;
        // When
        server.expect(requestTo(String.format("https://api.notion.com/v1/pages/%s", consultantId)))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        server.expect(requestTo("https://api.notion.com/v1/databases/1100064bbb9a806f9c81e1e478d7bbe0/query"))
                .andRespond(withSuccess(databaseResponse, MediaType.APPLICATION_JSON)); // Respond with a valid JSON

        // Then
        Consultant consultant = notionService.getConsultantById(consultantId, true);

        assertNotNull(consultant);
        assertEquals("Adam West", consultant.name());
        assertEquals(consultantId, consultant.uuid());

        // may want to assert responsiblePersonList content if applicable
        assertNotNull(consultant.responsiblePersonList());
        assertEquals(1, consultant.responsiblePersonList().size());
        assertEquals("Anne Mary", consultant.responsiblePersonList().getFirst().name()); // Adjust based on your ResponsiblePerson class structure
    }


}