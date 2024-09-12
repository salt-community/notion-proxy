package com.saltpgp.notionproxy.controller;

import com.saltpgp.notionproxy.dtos.outgoing.ConsultantDto;
import com.saltpgp.notionproxy.dtos.outgoing.DeveloperDto;
import com.saltpgp.notionproxy.dtos.outgoing.SaltiesDto;
import com.saltpgp.notionproxy.exceptions.NotionException;
import com.saltpgp.notionproxy.models.Consultant;
import com.saltpgp.notionproxy.models.Score;
import com.saltpgp.notionproxy.service.NotionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("notion")
@CrossOrigin
public class NotionController {

    private final NotionService notionService;

    public NotionController(NotionService notionService) {
        this.notionService = notionService;
    }

    @GetMapping("")
    public ResponseEntity<List<SaltiesDto>> getAllSalties() {
        try {
            return ResponseEntity.ok(SaltiesDto.fromModel(notionService.getSalties()));
        } catch (NotionException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("responsible/{id}")
    public ResponseEntity<ConsultantDto> getConsultant(
            @PathVariable UUID id,
            @RequestParam(required = false, defaultValue = "false") boolean includeNull) {
        try {
            Consultant consultant = notionService.getResponsiblePersonNameByUserId(id, includeNull);
            if (consultant == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(ConsultantDto.fromModel(consultant));
        } catch (NotionException e) {
            return ResponseEntity.internalServerError().build();
        } catch (HttpClientErrorException.NotFound e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("responsible")
    public ResponseEntity<List<ConsultantDto>> getConsultants(
            @RequestParam(required = false, defaultValue = "false") boolean includeEmpty,
            @RequestParam(required = false, defaultValue = "false") boolean includeNull) {
        try {
            return ResponseEntity.ok(notionService.getConsultants(includeEmpty, includeNull).stream().map(ConsultantDto::fromModel).toList());
        } catch (NotionException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("developer/{id}/score")
    public ResponseEntity <List<Score>> getScores(
            @PathVariable UUID id
    ){
        try{
            return ResponseEntity.ok(notionService.getDeveloperScores(id));
        } catch (NotionException e) {
            return ResponseEntity.internalServerError().build();
        }

    }



}
