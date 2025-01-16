package com.saltpgp.notionproxy.developer.controller;

import com.saltpgp.notionproxy.developer.controller.dtos.DeveloperDto;
import com.saltpgp.notionproxy.developer.service.DeveloperService;
import com.saltpgp.notionproxy.exceptions.NotionException;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<List<DeveloperDto>> getDevelopersList(
            @Parameter(description = "A filter to sort devs by current status(on assignment, pgp, etc)", required = false, example = "none")
            @RequestParam(required = false, defaultValue = "none") String filter) throws NotionException {
        log.info("Received request to get all developers with filter: {}", filter);
        return ResponseEntity.ok(DeveloperDto.fromModelList(developerService.getAllDevelopers(filter)));
    }
}
