package com.saltpgp.notionproxy.developer.controller;

import com.saltpgp.notionproxy.modules.developer.controller.DeveloperController;
import com.saltpgp.notionproxy.modules.developer.controller.dtos.DeveloperDto;
import com.saltpgp.notionproxy.modules.developer.model.Developer;
import com.saltpgp.notionproxy.modules.developer.service.DeveloperService;
import com.saltpgp.notionproxy.exceptions.NotionException;
import com.saltpgp.notionproxy.exceptions.NotionNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeveloperControllerTest {

    private DeveloperService developerService;

    private DeveloperController developerController;

    @BeforeEach
    void setUp() {
        developerService = mock(DeveloperService.class);
        developerController = new DeveloperController(developerService);
    }

    @Test
    void getDevelopersList() throws NotionException {
        // Arrange
        String status = "none";
        List<Developer> mockDevelopers = new ArrayList<>();
        mockDevelopers.add(new Developer("Jane Smith", UUID.randomUUID(), "https://github.com/janesmith", "https://github.com/janesmith.png", "janesmith@example.com", "PGP", "89", new ArrayList<>()));
        mockDevelopers.add(new Developer("John Doe", UUID.randomUUID(), "https://github.com/johndoe", "https://github.com/johndoe.png", "johndoe@example.com", "On Assignment", "95", new ArrayList<>()));

        when(developerService.getAllDevelopers(status,false)).thenReturn(mockDevelopers);


        // Act
        ResponseEntity<List<DeveloperDto>> response = developerController.getDevelopersList(status,false);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(developerService, times(1)).getAllDevelopers(status,false);
    }

    @Test
    void getDeveloper() throws NotionException, NotionNotFoundException {
        // Arrange
        UUID id = UUID.randomUUID();
        Developer mockDeveloper = new Developer("John Doe", id, "https://github.com/johndoe", "https://github.com/johndoe.png", "johndoe@example.com", "On Assignment", "95", new ArrayList<>());

        when(developerService.getDeveloperById(id,false)).thenReturn(mockDeveloper);

        // Act
        ResponseEntity<DeveloperDto> response = developerController.getDeveloper(id,false);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(DeveloperDto.fromModel(mockDeveloper), response.getBody());
        verify(developerService, times(1)).getDeveloperById(id,false);
    }
}
