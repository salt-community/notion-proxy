package com.saltpgp.notionproxy.staff.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saltpgp.notionproxy.api.bucket.BucketApiService;
import com.saltpgp.notionproxy.exceptions.NotionException;
import com.saltpgp.notionproxy.api.notion.NotionApiService;
import com.saltpgp.notionproxy.api.notion.filter.NotionServiceFilters;
import com.saltpgp.notionproxy.modules.staff.StaffFilter;
import com.saltpgp.notionproxy.modules.staff.StaffService;
import com.saltpgp.notionproxy.modules.staff.models.Staff;
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

    private static String sampleJson = """
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

    @Test
    void getAllCore_ShouldReturnAllCore() throws NotionException, JsonProcessingException {

        // Arrange

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode mockResponse = objectMapper.readTree(sampleJson);
        String filter = "none";

        when(mockApiService.fetchDatabase(mockCoreDatabaseId,
                NotionServiceFilters.filterBuilder(null, filter, StaffFilter.STAFF_FILTER))).thenReturn(mockResponse);

        List<Staff> expectedResponse = List.of(
                new Staff("John Doe", "john.doe@example.com", UUID.fromString("123e4567-e89b-12d3-a456-426614174000"), "Engineering"),
                new Staff("Jane Smith", "jane.smith@example.com", UUID.fromString("123e4567-e89b-12d3-a456-426614174001"), "Design")
        );

        // Act
        var result = mockStaffService.getAllCore(filter);

        // Assert
        Assertions.assertEquals(expectedResponse.getFirst().getName(), result.getFirst().getName());
        Assertions.assertEquals(expectedResponse.getLast().getEmail(), result.getLast().getEmail());
        Assertions.assertEquals(expectedResponse.getFirst().getId(), result.getFirst().getId());
        Assertions.assertEquals(expectedResponse.getLast().getRole(), result.getLast().getRole());
    }

    @Test
    void getStaffById_ShouldReturnStaff() throws NotionException, JsonProcessingException {

    }

}
