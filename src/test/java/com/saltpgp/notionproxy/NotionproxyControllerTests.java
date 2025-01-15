package com.saltpgp.notionproxy;

import com.saltpgp.notionproxy.config.SecurityConfig;
import com.saltpgp.notionproxy.controller.NotionController;
import com.saltpgp.notionproxy.models.Consultant;
import com.saltpgp.notionproxy.models.Developer;
import com.saltpgp.notionproxy.models.ResponsiblePerson;
import com.saltpgp.notionproxy.service.NotionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpServerErrorException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotionController.class)
@Import(SecurityConfig.class) // Import your security configuration
class NotionControllerTest {

    @Value("${NOTION_API_KEY}")
    private String TEST_API_KEY;

    @Value("${CUSTOM_API_KEY}")
    private String CUSTOM_API_KEY;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotionService notionService;

    @Test
    void getAllSalties_shouldReturnListOfSalties() throws Exception {

        // Arrange
        List<Developer> mockDevelopers = List.of(
                new Developer("Carolinne Melo", UUID.fromString("5f972ce7-b8f0-4b27-b97f-b860c9c2fe4c"), "https://github.com/carolinnemelo","https://github.com/carolinnemelo.png" , "carolinnepmelo@gmail.com", "Course", "100", List.of() ),
                new Developer("Carl-Henrik Alm", UUID.fromString("1450064b-bb9a-80a6-88c5-e9391cdd8974"), null, null, "carlhalm@gmail.com", "Course", "100", List.of())
        );

        String filter = "none";

        when(notionService.getAllDevelopers(filter)).thenReturn(mockDevelopers);

        String expectedResponse = """
        [
          {
            "name": "Carolinne Melo",
            "id": "5f972ce7-b8f0-4b27-b97f-b860c9c2fe4c",
            "email": "carolinnepmelo@gmail.com",
            "githubUrl": "https://github.com/carolinnemelo",
            "githubImageUrl": "https://github.com/carolinnemelo.png",
            "status": "Course",
            "totalScore": "100"
          },
          {
            "name": "Carl-Henrik Alm",
            "id": "1450064b-bb9a-80a6-88c5-e9391cdd8974",
            "email": "carlhalm@gmail.com",
            "githubUrl": null,
            "githubImageUrl": null,
            "status": "Course",
            "totalScore": "100"
          }
        ]
        """;

        // Act & Assert
        mockMvc.perform(get("/api/salt")
                        .header(CUSTOM_API_KEY, TEST_API_KEY)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    void getAllSalties_shouldReturnUnauthorized() throws Exception {

        // Act & Assert
        mockMvc.perform(get("/api/salt")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAllSalties_shouldReturnInternalServerError() throws Exception {

        // Arrange
        String filter = "none";

        when(notionService.getAllDevelopers(filter)).thenThrow(new RuntimeException());

        // Act and assert
        mockMvc.perform(get("/api/salt")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(CUSTOM_API_KEY, TEST_API_KEY))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getConsultants_shouldReturnListOfConsultantsWithEmptyResponsiblePerson() throws Exception {

        //Arrange
        List<ResponsiblePerson> mockResponsiblepersons = List.of(
                new ResponsiblePerson("TestResponsiblePerson1", UUID.fromString("b28fbeb3-f829-4d27-9339-3a41f8d45435"), "test@gmail.com", List.of())
        );

        List<Consultant> mockConsultants = List.of(
                new Consultant("TestName", UUID.fromString("f0d02a91-50c3-46a7-a4e7-76f8de3db2a9"), mockResponsiblepersons),
                new Consultant("TestName2", UUID.fromString("8ca26bfd-920d-4f46-b03d-5e485eb70504"), new ArrayList<>())
        );

        boolean includeEmptyResponsiblePersons = true;

        when(notionService.getAllConsultants(includeEmptyResponsiblePersons)).thenReturn(mockConsultants);

        String expectedResponse = """
        [
          {
            "name": "TestName",
            "id": "f0d02a91-50c3-46a7-a4e7-76f8de3db2a9",
            "responsiblePersonList": [
                {
                    "name": "TestResponsiblePerson1",
                    "id": "b28fbeb3-f829-4d27-9339-3a41f8d45435",
                    "email": "test@gmail.com"
                }
            ]
          },
          {
            "name": "TestName2",
            "id": "8ca26bfd-920d-4f46-b03d-5e485eb70504",
            "responsiblePersonList": []
          }
        ]
        """;

        // Act & Assert
        mockMvc.perform(get("/api/salt/consultants?includeEmptyResponsible=true")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(CUSTOM_API_KEY, TEST_API_KEY))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    void getConsultants_shouldReturnUnAuthorized() throws Exception{

        // Act & Assert
        mockMvc.perform(get("/api/salt/consultants")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void getConsultants_shouldReturnInternalServerError() throws Exception {

        // Arrange
        boolean includeEmptyResponsiblePersons = false;

        when(notionService.getAllConsultants(includeEmptyResponsiblePersons)).thenThrow(new RuntimeException());

        //Act & Assert
        mockMvc.perform(get("/api/salt/consultants")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(CUSTOM_API_KEY, TEST_API_KEY))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getConsultant_shouldReturnConsultant() throws Exception {

        // Arrange
        UUID consultantId = UUID.fromString("f0d02a91-50c3-46a7-a4e7-76f8de3db2a9");

        Consultant mockConsultant = new Consultant("TestDev", consultantId,
                List.of(new ResponsiblePerson("TestResp", UUID.fromString("8ca26bfd-920d-4f46-b03d-5e485eb70504"), "test@gmail.com", List.of()))
        );

        when(notionService.getConsultantById(consultantId)).thenReturn(mockConsultant);

        String expectedResponse = """
                {
                    "name": "TestDev",
                    "id": "f0d02a91-50c3-46a7-a4e7-76f8de3db2a9",
                    "responsiblePersonList": [
                        {
                            "name": "TestResp",
                            "id": "8ca26bfd-920d-4f46-b03d-5e485eb70504",
                            "email": "test@gmail.com"
                        }
                    ]
                }
                """;

        // Act & Assert
        mockMvc.perform(get("/api/salt/consultants/" + consultantId)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(CUSTOM_API_KEY, TEST_API_KEY))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    void getConsultant_shouldReturnUnAuthorized() throws Exception {

        // Act & Assert
        mockMvc.perform(get("/api/salt/consultants/" + UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getConsultant_shouldReturnInternalServerError() throws Exception {

        // Arrange
        UUID mockConsultantId = UUID.randomUUID();

        when(notionService.getConsultantById(mockConsultantId)).thenThrow(new RuntimeException());

        // Act & Assert
        mockMvc.perform(get("/api/salt/consultants/" + mockConsultantId)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(CUSTOM_API_KEY, TEST_API_KEY))
                .andExpect(status().isInternalServerError());

    }

    @Test
    void getResponsibleShouldReturnListOfResponsiblePersons() throws Exception {

        // Arrange
        List<ResponsiblePerson> mockResponsiblePersonList = List.of(
                new ResponsiblePerson("TestResponsible1", UUID.fromString("f0d02a91-50c3-46a7-a4e7-76f8de3db2a9"),"test@gmail.com", List.of()),
                new ResponsiblePerson("TestResponsible2", UUID.fromString("1450064b-bb9a-80a6-88c5-e9391cdd8974"),"test2@gmail.com", List.of())
        );

        boolean includeConsultants = false;

        when(notionService.getAllResponsiblePeople(includeConsultants)).thenReturn(mockResponsiblePersonList);

        String expectedResponse = """
                [
                    {
                        "name": "TestResponsible1",
                        "id": "f0d02a91-50c3-46a7-a4e7-76f8de3db2a9",
                        "email": "test@gmail.com"
                    },
                    {
                        "name": "TestResponsible2",
                        "id": "1450064b-bb9a-80a6-88c5-e9391cdd8974",
                        "email": "test2@gmail.com"
                    }
                ]
                """;

        // Act & Assert
        mockMvc.perform(get("/api/salt/responsible")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(CUSTOM_API_KEY, TEST_API_KEY))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    void getResponsible_shouldReturnUnAuthorized() throws Exception {

        // Act & Assert
        mockMvc.perform(get("/api/salt/responsible")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

    }

    @Test
    void getResponsible_shouldReturnInternalServerError() throws Exception {

        // Arrange
        boolean includeConsultants = false;

        when(notionService.getAllResponsiblePeople(includeConsultants)).thenThrow(new RuntimeException());

    }
}
