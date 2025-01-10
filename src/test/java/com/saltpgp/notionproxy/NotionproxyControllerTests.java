package com.saltpgp.notionproxy;

import com.saltpgp.notionproxy.config.SecurityConfig;
import com.saltpgp.notionproxy.controller.NotionController;
import com.saltpgp.notionproxy.models.Developer;
import com.saltpgp.notionproxy.service.NotionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotionController.class)
@Import(SecurityConfig.class) // Import your security configuration
 class NotionControllerTest {

    @Value("${CUSTOM_API_KEY}")
    private String TEST_API_KEY;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotionService notionService;

    @Test
    void getAllSalties_shouldReturnListOfSalties() throws Exception {

        // Arrange
        List<Developer> mockDevelopers = List.of(
                new Developer("Carolinne Melo", UUID.fromString("5f972ce7-b8f0-4b27-b97f-b860c9c2fe4c"), "https://github.com/carolinnemelo","https://github.com/carolinnemelo.png" , "carolinnepmelo@gmail.com", null),
                new Developer("Carl-Henrik Alm", UUID.fromString("1450064b-bb9a-80a6-88c5-e9391cdd8974"), null, null, "carlhalm@gmail.com", null)
        );

        when(notionService.getAllDevelopers()).thenReturn(mockDevelopers);

        String expectedResponse = """
        [
          {
            "name": "Carolinne Melo",
            "id": "5f972ce7-b8f0-4b27-b97f-b860c9c2fe4c",
            "email": "carolinnepmelo@gmail.com",
            "githubUrl": "https://github.com/carolinnemelo",
            "githubImageUrl": "https://github.com/carolinnemelo.png"
          },
          {
            "name": "Carl-Henrik Alm",
            "id": "1450064b-bb9a-80a6-88c5-e9391cdd8974",
            "email": "carlhalm@gmail.com",
            "githubUrl": null,
            "githubImageUrl": null
          }
        ]
        """;

        // Act & Assert
        mockMvc.perform(get("/salt")
                        .header("X-API-KEY", TEST_API_KEY)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }
}
