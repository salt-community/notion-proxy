package com.saltpgp.notionproxy.controller;


import com.saltpgp.notionproxy.service.NotionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<String> getNotion(@PathVariable UUID id) {
        notionService.getResponsiblePersonNameByUserId(id);
        return ResponseEntity.ok("Notion proxy available" );
    }

//    @GetMapping
//    public ResponseEntity<StudentDto> getStudentIds() {
//
//    }

}
