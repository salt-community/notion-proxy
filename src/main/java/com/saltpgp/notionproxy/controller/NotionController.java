package com.saltpgp.notionproxy.controller;

import com.saltpgp.notionproxy.dtos.outgoing.*;
import com.saltpgp.notionproxy.exceptions.NotionException;
import com.saltpgp.notionproxy.models.Consultant;
import com.saltpgp.notionproxy.models.ResponsiblePerson;
import com.saltpgp.notionproxy.service.NotionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("salt")
@CrossOrigin
public class NotionController {

    private final NotionService notionService;

    public NotionController(NotionService notionService) {
        this.notionService = notionService;
    }

    @GetMapping("")
    public ResponseEntity<List<SaltiesDto>> getAllSalties() {
        try {
            return ResponseEntity.ok(SaltiesDto.fromModel(notionService.getAllDevelopers()));
        } catch (NotionException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("consultants")
    public ResponseEntity<List<ConsultantWithResponsibleDto>> getConsultants(
            @RequestParam(required = false, defaultValue = "false") boolean includeEmpty,
            @RequestParam(required = false, defaultValue = "false") boolean includeNull) {
        try {
            return ResponseEntity.ok(notionService
                    .getAllConsultants(includeEmpty, includeNull).stream().map(ConsultantWithResponsibleDto::fromModel).toList());
        } catch (NotionException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("consultants/{id}")
    public ResponseEntity<ConsultantWithResponsibleDto> getConsultant(
            @PathVariable UUID id,
            @RequestParam(required = false, defaultValue = "false") boolean includeNull) {
        try {
            Consultant consultant = notionService.getConsultantById(id, includeNull);
            if (consultant == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(ConsultantWithResponsibleDto.fromModel(consultant));
        } catch (NotionException e) {
            return ResponseEntity.internalServerError().build();
        } catch (HttpClientErrorException.NotFound e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("responsible")
    public <T> ResponseEntity<List<T>> getResponsiblePeople(
            @RequestParam(required = false, defaultValue = "false") boolean includeNull,
            @RequestParam(required = false, defaultValue = "false") boolean includeConsultants
    ) {
        try {
            if(includeConsultants){
                List<ResponsibleWithConsultantsDto> dtos = ResponsibleWithConsultantsDto
                        .fromModelSet(notionService.getAllResponsiblePeople(includeNull, includeConsultants));
                return ResponseEntity.ok((List<T>) dtos);
            }
            else{
                List<BasicResponsiblePersonDto> dtos = BasicResponsiblePersonDto
                        .fromModelSet(notionService.getAllResponsiblePeople(includeNull, includeConsultants));
                return ResponseEntity.ok((List<T>) dtos);
            }
        } catch (NotionException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("responsible/{id}")
    public <T> ResponseEntity<T> getResponsiblePeopleById(
            @PathVariable UUID id,
            @RequestParam(required = false, defaultValue = "false") boolean includeNull,
            @RequestParam(required = false, defaultValue = "false") boolean includeConsultants
    ) {
        try {
            ResponsiblePerson responsiblePerson = notionService.getResponsiblePersonById(id, includeNull, true);
            if (responsiblePerson == null) {
                return ResponseEntity.notFound().build();
            }

            if(includeConsultants){
                ResponsibleWithConsultantsDto dtos = ResponsibleWithConsultantsDto
                        .fromModel(responsiblePerson);
                return ResponseEntity.ok((T) dtos);
            }
            else{
                BasicResponsiblePersonDto dtos = BasicResponsiblePersonDto
                        .fromModel(responsiblePerson);
                return ResponseEntity.ok((T) dtos);
            }
        } catch (NotionException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("developers/{id}/scores")
    public ResponseEntity <DeveloperDto> getScoreCard(@PathVariable UUID id){
        try{
            return ResponseEntity.ok(DeveloperDto.fromModel(notionService.getDeveloperByIdWithScore(id)));
        } catch (NotionException e) {
            return ResponseEntity.internalServerError().build();
        }

    }

}
