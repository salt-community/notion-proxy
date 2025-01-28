package com.saltpgp.notionproxy.staff;


import com.saltpgp.notionproxy.assignment.controller.dtos.DeveloperAssignmentsDto;
import com.saltpgp.notionproxy.exceptions.NotionException;
import com.saltpgp.notionproxy.exceptions.NotionNotFoundException;
import com.saltpgp.notionproxy.staff.dtos.StaffConsultantDto;
import com.saltpgp.notionproxy.staff.dtos.StaffDto;
import com.saltpgp.notionproxy.staff.models.Staff;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/staff")
@CrossOrigin
@Slf4j
public class StaffController {

    private StaffService staffService;

    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @Operation(summary = "Get all staff at salt",
            description = "Returns all staff with details such as staffId, name, email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of staff",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StaffDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("")
    public ResponseEntity<List<StaffDto>> getAllStaff(
            @Parameter(description = "A filter to sort staff by role", required = false, example = "none")
            @RequestParam(required = false, defaultValue = "none") String filter) throws NotionException {
        return ResponseEntity.ok(staffService
                .getAllCore(filter)
                .stream()
                .map(StaffDto::fromModel)
                .toList());
    }

    @Operation(summary = "Returns 1 staff",
            description = "Returns a single staff by staffID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of staff",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StaffDto.class))),
            @ApiResponse(responseCode = "404", description = "Staff not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<StaffDto> getStaffById(@PathVariable UUID id) throws NotionException, NotionNotFoundException {
        return ResponseEntity.ok(StaffDto.fromModel(staffService.getStaffById(id)));
    }

    @Operation(summary = "Returns consultants the staff is responsible for",
            description = "Returns a list of consultants, can return empty list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of consultants"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}/consultants")
    public ResponseEntity<List<StaffConsultantDto>> getConsultants(@PathVariable UUID id) throws NotionException {
        return ResponseEntity.ok(staffService
                .getStaffConsultants(id)
                .stream()
                .map(StaffConsultantDto::fromModel)
                .toList());
    }
}
