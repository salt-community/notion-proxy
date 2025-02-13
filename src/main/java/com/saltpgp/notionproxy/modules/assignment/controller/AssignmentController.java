package com.saltpgp.notionproxy.modules.assignment.controller;

import com.saltpgp.notionproxy.exceptions.NotionNotFoundException;
import com.saltpgp.notionproxy.modules.assignment.controller.dtos.AssignmentDto;
import com.saltpgp.notionproxy.modules.assignment.controller.dtos.DeveloperAssignmentsDto;
import com.saltpgp.notionproxy.modules.assignment.model.Assignment;
import com.saltpgp.notionproxy.modules.assignment.service.AssignmentService;
import com.saltpgp.notionproxy.exceptions.NotionException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/assignments")
@Tag(name = "Assignments", description = "APIs for managing assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @Operation(summary = "Get assignments by developer ID",
            description = "Retrieve all assignments associated with a specific developer by their unique developer ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of assignments",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DeveloperAssignmentsDto.class))),
            @ApiResponse(responseCode = "404", description = "Developer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping()
    public ResponseEntity<DeveloperAssignmentsDto> getAssignmentsByUserId(
            @RequestParam(value = "developerId", required = true) UUID developerId,
            @RequestParam(value = "useCache", required = false, defaultValue = "true") boolean useCache) throws NotionException, NotionNotFoundException {
        List<Assignment> assignments = assignmentService.getAssignmentsFromDeveloper(developerId, useCache);
        return ResponseEntity.ok(new DeveloperAssignmentsDto(developerId.toString(), AssignmentDto.fromModelList(assignments)));
    }

    @Operation(summary = "Get assignment by assignment ID",
            description = "Retrieve details of a specific assignment by its unique ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of the assignment",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AssignmentDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid assignment ID supplied"),
            @ApiResponse(responseCode = "404", description = "Assignment not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AssignmentDto> getAssignmentByAssignmentId(
            @PathVariable String id,
            @RequestParam(value = "useCache", required = false, defaultValue = "true") boolean useCache) throws NotionException, NotionNotFoundException {
        return ResponseEntity.ok(AssignmentDto.fromModel(assignmentService.getAssignment(id, useCache)));
    }
}
