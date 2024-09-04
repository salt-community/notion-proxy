package com.saltpgp.notionproxy.controller;


import com.saltpgp.notionproxy.exceptions.NotionExceptions;
import com.saltpgp.notionproxy.models.Consultant;
import com.saltpgp.notionproxy.service.NotionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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


    @GetMapping("{id}")
    public ResponseEntity<List<String>> getNotion(@PathVariable UUID id) throws NotionExceptions {
        return ResponseEntity.ok(notionService.getResponsiblePersonNameByUserId(id));
    }

    @GetMapping("consultants")
    public ResponseEntity<List<Consultant>> getConsultants() {
        return ResponseEntity.ok(notionService.getConsultants());
    }


}
