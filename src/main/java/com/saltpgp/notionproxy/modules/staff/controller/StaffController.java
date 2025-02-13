package com.saltpgp.notionproxy.modules.staff.controller;


import com.saltpgp.notionproxy.exceptions.NotionException;
import com.saltpgp.notionproxy.exceptions.NotionNotFoundException;
import com.saltpgp.notionproxy.modules.staff.service.StaffService;
import com.saltpgp.notionproxy.modules.staff.controller.dtos.StaffConsultantDto;
import com.saltpgp.notionproxy.modules.staff.controller.dtos.StaffDto;
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

    private final StaffService staffService;

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
            @RequestParam(required = false, defaultValue = "none") String role,
            @RequestParam(value = "useCache", required = false, defaultValue = "true") boolean useCache) throws NotionException, NotionNotFoundException {
        return ResponseEntity.ok(staffService
                .getAllCore(role, useCache)
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
    public ResponseEntity<StaffDto> getStaffById(
            @PathVariable UUID id,
            @RequestParam(value = "useCache", required = false, defaultValue = "true") boolean useCache) throws NotionException, NotionNotFoundException {
        return ResponseEntity.ok(StaffDto.fromModel(staffService.getStaffById(id, useCache)));
    }

    @Operation(summary = "Returns consultants the staff is responsible for",
            description = "Returns a list of consultants, can return empty list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of consultants"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}/consultants")
    public ResponseEntity<List<StaffConsultantDto>> getConsultants(
            @PathVariable UUID id,
            @RequestParam(value = "useCache", required = false, defaultValue = "true") boolean useCache) throws NotionException, NotionNotFoundException {
        return ResponseEntity.ok(staffService
                .getStaffConsultants(id, useCache)
                .stream()
                .map(StaffConsultantDto::fromModel)
                .toList());
    }
}
