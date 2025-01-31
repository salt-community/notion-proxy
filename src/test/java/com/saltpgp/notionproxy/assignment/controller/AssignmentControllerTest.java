package com.saltpgp.notionproxy.assignment.controller;

import com.saltpgp.notionproxy.exceptions.NotionException;
import com.saltpgp.notionproxy.modules.assignment.controller.AssignmentController;
import com.saltpgp.notionproxy.modules.assignment.controller.dtos.DeveloperAssignmentsDto;
import com.saltpgp.notionproxy.modules.assignment.model.Assignment;
import com.saltpgp.notionproxy.modules.assignment.service.AssignmentService;
import jdk.jfr.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AssignmentControllerTest {

    private AssignmentService assignmentService;
    private AssignmentController assignmentController;

    @BeforeEach
    void setUp() {
        assignmentService = mock(AssignmentService.class);
        assignmentController = new AssignmentController(assignmentService);
    }

    @Test
    void getAssignmentsByDeveloperId() throws NotionException {
        // Arrange
        UUID developerId = UUID.randomUUID();
        List<Assignment> mockedAssignments = List.of(
                new Assignment("1", "Assignment 1", 90, List.of("Category1", "Category2"), "Great work!"),
                new Assignment("2", "Assignment 2", 80, List.of("Category3"), "Needs improvement")
        );
        when(assignmentService.getAssignmentsFromDeveloper(developerId, true)).thenReturn(mockedAssignments);

        // Act
        ResponseEntity<DeveloperAssignmentsDto> response = assignmentController.getAssignmentsByUserId(developerId, true);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, Objects.requireNonNull(response.getBody()).assignments().size());
        assertEquals(developerId.toString(), response.getBody().developerId());
        verify(assignmentService, times(1)).getAssignmentsFromDeveloper(developerId, true);
    }

}
