package com.saltpgp.notionproxy.modules.idcard.controller;

import com.saltpgp.notionproxy.exceptions.NotionException;
import com.saltpgp.notionproxy.exceptions.NotionNotFoundException;
import com.saltpgp.notionproxy.modules.idcard.controller.dtos.UserDto;
import com.saltpgp.notionproxy.modules.idcard.model.User;
import com.saltpgp.notionproxy.modules.idcard.service.IdCardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IdCardControllerTest {
    private IdCardService idCardService;

    private IdCardController idCardController;

    @BeforeEach
    void setUp() {
        idCardService = mock(IdCardService.class);
        idCardController = new IdCardController(idCardService);
    }

    @Test
    void getUserEmail() throws NotionException, NotionNotFoundException {
        // Arrange
        String email = "john.doe@example.com";
        User mockUser = new User("123e4567-e89b-12d3-a456-426614174000","John Doe", "Computer Science", "john.doe@example.com", "john-doe");

        when(idCardService.getIdCardEmail(email,false)).thenReturn(mockUser);


        // Act
        ResponseEntity<UserDto> response = idCardController.getIdCardEmail(email,false);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(UserDto.fromModel(mockUser), response.getBody());
        verify(idCardService, times(1)).getIdCardEmail(email,false);
    }

    @Test
    void getUserUuid() throws NotionException, NotionNotFoundException {
        // Arrange
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        User mockUser = new User(id.toString(),"John Doe", "Computer Science", "john.doe@example.com", "john-doe");

        when(idCardService.getIdCardUuid(id,false)).thenReturn(mockUser);

        // Act
        ResponseEntity<UserDto> response = idCardController.getIdCardUuid(id,false);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(UserDto.fromModel(mockUser), response.getBody());
        verify(idCardService, times(1)).getIdCardUuid(id,false);
    }
}