package com.saltpgp.notionproxy.controller;

import com.saltpgp.notionproxy.exceptions.NotionException;
import com.saltpgp.notionproxy.models.Consultant;
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
    public ResponseEntity<List<String>> getNotion(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(notionService.getResponsiblePersonNameByUserId(id));
        }
        catch (NotionException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping()
    public ResponseEntity<List<Consultant>> getConsultants() {
        try {
            return ResponseEntity.ok(notionService.getConsultants());
        } catch (NotionException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}
