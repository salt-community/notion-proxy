package com.saltpgp.notionproxy.controller;

import com.saltpgp.notionproxy.dtos.ConsultantDto;
import com.saltpgp.notionproxy.exceptions.NotionException;
import com.saltpgp.notionproxy.service.NotionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("responsible")
@CrossOrigin
public class NotionController {

    private final NotionService notionService;

    public NotionController(NotionService notionService) {
        this.notionService = notionService;
    }

    @GetMapping("{id}")
    public ResponseEntity<ConsultantDto> getNotion(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(ConsultantDto.fromModel(notionService.getResponsiblePersonNameByUserId(id)));
        }
        catch (NotionException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping()
    public ResponseEntity<List<ConsultantDto>> getConsultants() {
        try {
            return ResponseEntity.ok(notionService.getConsultants().stream().map(ConsultantDto::fromModel).toList());
        } catch (NotionException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}
