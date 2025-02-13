package com.saltpgp.notionproxy.staff.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saltpgp.notionproxy.api.bucket.BucketApiService;
import com.saltpgp.notionproxy.api.notion.filter.NotionProperty.NotionPropertyFilter;
import com.saltpgp.notionproxy.api.notion.filter.NotionProperty.PeopleFilter;
import com.saltpgp.notionproxy.exceptions.NotionException;
import com.saltpgp.notionproxy.api.notion.NotionApiService;
import com.saltpgp.notionproxy.api.notion.filter.NotionServiceFilters;
import com.saltpgp.notionproxy.exceptions.NotionNotFoundException;
import com.saltpgp.notionproxy.modules.staff.service.StaffProperty;
import com.saltpgp.notionproxy.modules.staff.service.StaffService;
import com.saltpgp.notionproxy.modules.staff.model.Staff;
import com.saltpgp.notionproxy.modules.staff.model.Consultant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StaffServiceTest {

    NotionApiService mockApiService;

    StaffService mockStaffService;
    BucketApiService mockBucketApiService;

    @Value("${CORE_DATABASE_ID}")
    private String mockCoreDatabaseId;
    @Value("${DATABASE_ID}")
    private String mockDeveloperDatabaseId;

    @BeforeEach
    void setUp() {
        mockBucketApiService = mock(BucketApiService.class);
        mockApiService = mock(NotionApiService.class);
        mockStaffService = new StaffService(mockApiService,mockCoreDatabaseId, mockDeveloperDatabaseId, mockBucketApiService);
    }

    private static final String sampleStaffJson = """
            {
                "results": [
                    {
                        "properties": {
                            "Person": {
                                "people": [
                                    {
                                        "name": "John Doe",
                                        "person": {
                                            "email": "john.doe@example.com"
                                        },
                                        "id": "123e4567-e89b-12d3-a456-426614174000"
                                    }
                                ]
                            },
                            "Guild": {
                                "multi_select": [
                                    {
                                        "name": "Engineering"
                                    }
                                ]
                            }
                        }
                    },
                    {
                        "properties": {
                            "Person": {
                                "people": [
                                    {
                                        "name": "Jane Smith",
                                        "person": {
                                            "email": "jane.smith@example.com"
                                        },
                                        "id": "123e4567-e89b-12d3-a456-426614174001"
                                    }
                                ]
                            },
                            "Guild": {
                                "multi_select": [
                                    {
                                        "name": "Design"
                                    }
                                ]
                            }
                        }
                    }
                ],
                "next_cursor": "cursor_string",
                "has_more": false
            }
        """;

    private static final String sampleConsultanteloperJson = """
    {
      "object": "list",
      "results": [
        {
          "object": "page",
          "id": "550e8400-e29b-41d4-a716-446655440000",
          "properties": {
            "Name": {
              "title": [
                {
                  "plain_text": "John Doe"
                }
              ]
            },
            "Email": {
              "email": "john.doe@example.com"
            }
          }
        },
        {
          "object": "page",
          "id": "123e4567-e89b-12d3-a456-426614174000",
          "properties": {
            "Name": {
              "title": [
                {
                  "plain_text": "Jane Smith"
                }
              ]
            },
            "Email": {
              "email": "jane.smith@example.com"
            }
          }
        }
      ],
      "next_cursor": "some-cursor-token",
      "has_more": false
    }            
""";

    @Test
    void getAllCore_ShouldReturnAllCore() throws NotionException, JsonProcessingException {

        // Arrange

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode mockResponse = objectMapper.readTree(sampleStaffJson);
        String filter = "none";

        when(mockApiService.fetchDatabase(mockCoreDatabaseId,
                NotionServiceFilters.filterBuilder(null, filter, StaffProperty.StaffFilter.STAFF_FILTER))).thenReturn(mockResponse);

        List<Staff> expectedResponse = List.of(
                new Staff("John Doe", "john.doe@example.com", UUID.fromString("123e4567-e89b-12d3-a456-426614174000"), "Engineering"),
                new Staff("Jane Smith", "jane.smith@example.com", UUID.fromString("123e4567-e89b-12d3-a456-426614174001"), "Design")
        );

        // Act
        List<Staff> result = mockStaffService.getAllCore(filter, false);

        // Assert
        Assertions.assertEquals(expectedResponse.getFirst().getName(), result.getFirst().getName());
        Assertions.assertEquals(expectedResponse.getLast().getEmail(), result.getLast().getEmail());
        Assertions.assertEquals(expectedResponse.getFirst().getId(), result.getFirst().getId());
        Assertions.assertEquals(expectedResponse.getLast().getRole(), result.getLast().getRole());
    }

    @Test
    void getStaffById_ShouldReturnStaff() throws NotionException, JsonProcessingException, NotionNotFoundException {

        // Arrange
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode mockResponse = objectMapper.readTree(sampleStaffJson);

        UUID testUUID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        Staff expected =  new Staff("John Doe", "john.doe@example.com", testUUID, "Engineering");

        NotionPropertyFilter filter = NotionPropertyFilter.peopleFilter(PeopleFilter.CONTAINS,testUUID.toString(),"Person");

        when(mockApiService.fetchDatabase(mockCoreDatabaseId, NotionServiceFilters.filterBuilder(null, filter))).thenReturn(mockResponse);

        // Act
        var result = mockStaffService.getStaffById(testUUID, false);

        // Assert
        Assertions.assertEquals(expected.getName(), result.getName());
        Assertions.assertEquals(expected.getEmail(), result.getEmail());
        Assertions.assertEquals(expected.getRole(), result.getRole());
        Assertions.assertEquals(expected.getId(), result.getId());
    }

    @Test
    void getStaffConsultants_ShouldReturnConsultants() throws JsonProcessingException, NotionException, NotionNotFoundException {

        // Arrange
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode mockResponse = objectMapper.readTree(sampleConsultanteloperJson);

        UUID testUUID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        List<Consultant> expectedResponse = List.of(
                new Consultant("John Doe", "john.doe@example.com", UUID.fromString("550e8400-e29b-41d4-a716-446655440000")),
                new Consultant("Jane Smith", "jane.smith@example.com", UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
        );

        String filter = NotionServiceFilters.filterBuilder(null, testUUID.toString() , StaffProperty.StaffFilter.STAFF_FILTER_RESPONSIBLE);
        when(mockApiService.fetchDatabase(mockDeveloperDatabaseId, filter)).thenReturn(mockResponse);

        // Act
        List<Consultant> result = mockStaffService.getStaffConsultants(testUUID, false);

        // Assert
        Assertions.assertEquals(expectedResponse.getFirst().getName(), result.getFirst().getName());
        Assertions.assertEquals(expectedResponse.getLast().getId(), result.getLast().getId());
        Assertions.assertEquals(expectedResponse.getFirst().getEmail(), result.getFirst().getEmail());
    }

}
