package com.saltpgp.notionproxy.modules.idcard.controller;

import com.saltpgp.notionproxy.exceptions.NotionException;
import com.saltpgp.notionproxy.exceptions.NotionNotFoundException;
import com.saltpgp.notionproxy.modules.idcard.controller.dtos.UserDto;
import com.saltpgp.notionproxy.modules.idcard.service.IdCardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/id-cards")
@CrossOrigin
@Slf4j
@Tag(name = "Id Card", description = "APIs for getting user to id card")
public class IdCardController {
    private final IdCardService idCardService;

    public IdCardController(IdCardService idCardService) {
        this.idCardService = idCardService;
    }

    @GetMapping("email/{email}")
    @Operation(summary = "Get a specific user by email",
            description = "Retrieve details of a user by their unique email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user"),
            @ApiResponse(responseCode = "404", description = "Developer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<UserDto> getIdCardEmail(
            @PathVariable String email,
            @RequestParam(value = "useCache", required = false, defaultValue = "true") boolean useCache)
            throws NotionException, NotionNotFoundException {
        log.info("Request received to get user with email: {}", email);
        return ResponseEntity.ok(UserDto.fromModel(idCardService.getIdCard(email, useCache)));
    }

    @GetMapping("private-email/{private-email}")
    @Operation(summary = "Get a specific user by private email",
            description = "Retrieve details of a user by their unique private email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user"),
            @ApiResponse(responseCode = "404", description = "Developer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<UserDto> getIdCardPrivateEmail(
            @PathVariable("private-email") String privateEmail,
            @RequestParam(value = "useCache", required = false, defaultValue = "true") boolean useCache)
            throws NotionException, NotionNotFoundException {
        log.info("Request received to get user with private email: {}", privateEmail);
        return ResponseEntity.ok(UserDto.fromModel(idCardService.getIdCard(privateEmail, useCache)));
    }

    @GetMapping("uuid/{uuid}")
    @Operation(summary = "Get a specific user by uuid",
            description = "Retrieve details of a user by their unique uuid.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user"),
            @ApiResponse(responseCode = "404", description = "Developer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<UserDto> getIdCardUuid(
            @PathVariable String uuid,
            @RequestParam(value = "useCache", required = false, defaultValue = "true") boolean useCache)
            throws NotionException, NotionNotFoundException {
        log.info("Request received to get user with uuid: {}", uuid);
        return ResponseEntity.ok(UserDto.fromModel(idCardService.getIdCard(uuid, useCache)));
    }
}
