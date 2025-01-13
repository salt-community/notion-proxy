package com.saltpgp.notionproxy.controller;

import com.saltpgp.notionproxy.dtos.outgoing.*;
import com.saltpgp.notionproxy.exceptions.NotionException;
import com.saltpgp.notionproxy.exceptions.NotionNotFoundException;
import com.saltpgp.notionproxy.models.Consultant;
import com.saltpgp.notionproxy.models.ResponsiblePerson;
import com.saltpgp.notionproxy.service.NotionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/salt")
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
    @GetMapping("")
    public ResponseEntity<List<SaltiesDto>> getAllSalties() throws NotionException {
        return ResponseEntity.ok(SaltiesDto.fromModel(notionService.getAllDevelopers()));
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

        Consultant consultant = notionService.getConsultantById(id);
        if (consultant == null) {
            log.warn("Consultant not found for ID: {}", id);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ConsultantWithResponsibleDto.fromModel(consultant));
    }

    @Operation(summary = "Get all responsible persons", description = "Retrieves a list of all responsible persons, with an optional filter to include consultants.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of responsible persons"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("responsible")
    public <T> ResponseEntity<List<T>> getResponsiblePeople(
            @Parameter(description = "Whether to include the consultants for whom they are responsible", required = false, example = "false")
            @RequestParam(required = false, defaultValue = "false") boolean includeConsultants) throws NotionException {

        log.info("Received request to get responsible persons with includeConsultants={}", includeConsultants);

        if (includeConsultants) {
            log.info("Including consultants in the list of responsible persons.");
            List<ResponsibleWithConsultantsDto> dtos = ResponsibleWithConsultantsDto
                    .fromModelList(notionService.getAllResponsiblePeople(true));
            log.debug("Returning response with {} responsible persons with consultants.", dtos.size());
            return ResponseEntity.ok((List<T>) dtos);
        } else {
            log.info("Excluding consultants from the list of responsible persons.");
            List<BasicResponsiblePersonDto> dtos = BasicResponsiblePersonDto
                    .fromModelList(notionService.getAllResponsiblePeople(false));
            log.debug("Returning response with {} responsible persons without consultants.", dtos.size());
            return ResponseEntity.ok((List<T>) dtos);
        }

    }

    @Operation(summary = "Get a specific responsible person by ID", description = "Retrieves details of a specific responsible person by their unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the responsible person"),
            @ApiResponse(responseCode = "404", description = "Responsible person not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("responsible/{id}")
    public <T> ResponseEntity<T> getResponsiblePeopleById(
            @Parameter(description = "UUID of the responsible person to retrieve", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Whether to include the consultants for whom they are responsible", required = false, example = "false")
            @RequestParam(required = false, defaultValue = "false") boolean includeConsultants) throws NotionException {

            log.info("Received request to get responsible person with ID: {}. Include consultants: {}", id, includeConsultants);
            ResponsiblePerson responsiblePerson = notionService
                    .getResponsiblePersonById(id, includeConsultants);

            if (responsiblePerson == null) {
                log.warn("Responsible person not found for ID: {}", id);
                return ResponseEntity.notFound().build();
            }

            if (includeConsultants) {
                ResponsibleWithConsultantsDto dtos = ResponsibleWithConsultantsDto
                        .fromModel(responsiblePerson);
                log.info("Successfully retrieved responsible person with consultants for ID: {}", id);
                return ResponseEntity.ok((T) dtos);
            } else {
                BasicResponsiblePersonDto dtos = BasicResponsiblePersonDto
                        .fromModel(responsiblePerson);
                log.info("Successfully retrieved basic responsible person for ID: {}", id);
                return ResponseEntity.ok((T) dtos);
            }

    }

    @Operation(summary = "Get developer scorecard", description = "Retrieves the scorecard of a specific developer.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the developer scorecard"),
            @ApiResponse(responseCode = "404", description = "Developer not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("developers/{id}/scores")
    public ResponseEntity<DeveloperDto> getScoreCard(@PathVariable UUID id) throws NotionException, NotionNotFoundException {
            return ResponseEntity.ok(DeveloperDto.fromModel(notionService.getDeveloperByIdWithScore(id)));

    }
}
