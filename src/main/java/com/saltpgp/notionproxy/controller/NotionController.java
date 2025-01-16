package com.saltpgp.notionproxy.controller;

import com.saltpgp.notionproxy.dtos.outgoing.*;
import com.saltpgp.notionproxy.exceptions.NotionException;
import com.saltpgp.notionproxy.exceptions.NotionNotFoundException;
import com.saltpgp.notionproxy.models.ResponsiblePerson;
import com.saltpgp.notionproxy.service.NotionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api")
@CrossOrigin
@Slf4j
public class NotionController {

    private final NotionService notionService;

    public NotionController(NotionService notionService) {
        this.notionService = notionService;
    }

    @Operation(summary = "Get all developers", description = "Retrieves a list of all developers.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of developers"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("developers")
    public ResponseEntity<List<DeveloperDto>> getDevelopersList(
            @Parameter(description = "A filter to sort devs by current status(on assignment, pgp, etc)", required = false, example = "none")
            @RequestParam(required = false, defaultValue = "none") String filter) throws NotionException {
        log.info("Received request to get all developers with filter: {}", filter);
        return ResponseEntity.ok(DeveloperDto.fromModelList(notionService.getAllDevelopers(filter)));
    }

    @GetMapping("developers/{id}")
    public ResponseEntity<DeveloperDto> getDeveloper(@PathVariable UUID id) throws NotionException{
        log.info("Received request for developer with ID: {}", id);
        return ResponseEntity.ok(DeveloperDto.fromModel(notionService.getDeveloperById(id)));
    }

    @Operation(summary = "Get developer scorecard", description = "Retrieves the scorecard of a specific developer.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the developer scorecard"),
            @ApiResponse(responseCode = "404", description = "Developer not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("developers/{id}/scores")
    public ResponseEntity<DeveloperDto> getScoreCard(@PathVariable UUID id) throws NotionException, NotionNotFoundException {
        log.info("Received request for developer scorecard with ID: {}", id);
        return ResponseEntity.ok(DeveloperDto.fromModel(notionService.getDeveloperByIdWithScore(id)));
    }

    @Operation(summary = "Get all consultants", description = "Retrieves a list of all consultants, with an optional filter to include consultants with empty responsible persons.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of consultants"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("consultants")
    public ResponseEntity<List<ConsultantWithResponsibleDto>> getConsultants(
            @Parameter(description = "Whether to include consultants with no responsible persons", required = false, example = "false")
            @RequestParam(required = false, defaultValue = "false") boolean includeEmptyResponsible) throws NotionException {

        log.info("Received request to get all consultants. Include empty responsible: {}", includeEmptyResponsible);
        return ResponseEntity.ok(notionService
                .getAllConsultants(includeEmptyResponsible)
                .stream()
                .map(ConsultantWithResponsibleDto::fromModel)
                .toList());
    }

    @Operation(summary = "Get a specific consultant by ID", description = "Retrieves details of a specific consultant by their unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the consultant"),
            @ApiResponse(responseCode = "404", description = "Consultant not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("consultants/{id}")
    public ResponseEntity<ConsultantWithResponsibleDto> getConsultant(
            @Parameter(description = "UUID of the consultant to retrieve", required = true)
            @PathVariable UUID id) throws NotionException {
        log.info("Received request to get consultant with ID: {}", id);
        return ResponseEntity.ok(ConsultantWithResponsibleDto.fromModel(notionService.getConsultantById(id)));
    }

    @Operation(summary = "Get all responsible persons", description = "Retrieves a list of all responsible persons, with an optional filter to include consultants.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of responsible persons"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("responsible")
    public ResponseEntity<List<?>> getResponsiblePeople(
            @Parameter(description = "Whether to include the consultants for whom they are responsible", required = false, example = "false")
            @RequestParam(required = false, defaultValue = "false") boolean includeConsultants) throws NotionException {
        log.info("Received request to get responsible persons with includeConsultants={}", includeConsultants);
        var responsiblePersonList = notionService.getAllResponsiblePeople(includeConsultants);
        return ResponseEntity.ok(includeConsultants ?
                ResponsibleWithConsultantsDto.fromModelList(responsiblePersonList) :
                BasicResponsiblePersonDto.fromModelList(responsiblePersonList));
    }

    @Operation(summary = "Get a specific responsible person by ID", description = "Retrieves details of a specific responsible person by their unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the responsible person"),
            @ApiResponse(responseCode = "404", description = "Responsible person not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("responsible/{id}")
    public ResponseEntity<?> getResponsiblePeopleById(
            @Parameter(description = "UUID of the responsible person to retrieve", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Whether to include the consultants for whom they are responsible", required = false, example = "false")
            @RequestParam(required = false, defaultValue = "false") boolean includeConsultants) throws NotionException {

        log.info("Received request to get responsible person with ID: {}. Include consultants: {}", id, includeConsultants);
        var responsiblePerson = notionService
                .getResponsiblePersonById(id, includeConsultants);
        return ResponseEntity.ok(includeConsultants ?
                ResponsibleWithConsultantsDto.fromModel(responsiblePerson) :
                BasicResponsiblePersonDto.fromModel(responsiblePerson));
    }
}
