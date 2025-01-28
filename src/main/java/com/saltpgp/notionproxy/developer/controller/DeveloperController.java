package com.saltpgp.notionproxy.developer.controller;

import com.saltpgp.notionproxy.developer.controller.dtos.DeveloperDto;
import com.saltpgp.notionproxy.developer.service.DeveloperService;
import com.saltpgp.notionproxy.exceptions.NotionException;
import com.saltpgp.notionproxy.exceptions.NotionNotFoundException;
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
@RequestMapping("api/developers")
@CrossOrigin
@Slf4j
public class DeveloperController {

    private final DeveloperService developerService;

    public DeveloperController(DeveloperService developerService) {
        this.developerService = developerService;
    }

    @GetMapping()
    @Operation(summary = "Get a list of developers",
            description = "Retrieve a list of all developers. An optional filter can be applied to sort by status. " +
                    "Valid filter values: 'On Assignment', 'PGP', 'Done', 'None', 'LetGo', 'Talent Pool' or 'Precourse'. " +
                    "The filter is case sensitive so ensure that the values for the filter parameter spelled exactly as provided" +
                    "If no filter parameter is provided, all developers will be returned.  "
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the developers list"),
            @ApiResponse(responseCode = "400", description = "Invalid filter value"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })

    public ResponseEntity<List<DeveloperDto>> getDevelopersList(
            @Parameter(description = "A filter to sort devs by current status(On Assignment, PGP, etc) It is case sensitive",
                    example = "none")
            @RequestParam(required = false, defaultValue = "none") String status,
            @RequestParam(value = "useCache", required = false, defaultValue = "true") boolean useCache) throws NotionException {
        log.info("Request received to get developers list with filter: {}", status);
        return ResponseEntity.ok(DeveloperDto.fromModelList(developerService.getAllDevelopers(status, useCache)));
    }

    @GetMapping("{id}")
    @Operation(summary = "Get a specific developer by ID",
            description = "Retrieve details of a developer by their unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved developer"),
            @ApiResponse(responseCode = "404", description = "Developer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<DeveloperDto> getDeveloper(
            @PathVariable UUID id,
            @RequestParam(value = "useCache", required = false, defaultValue = "true") boolean useCache)
            throws NotionException, NotionNotFoundException {
        log.info("Request received to get developer with ID: {}", id);
        return ResponseEntity.ok(DeveloperDto.fromModel(developerService.getDeveloperById(id, useCache)));
    }

}
