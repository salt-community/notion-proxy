package com.saltpgp.notionproxy.controller;


import com.saltpgp.notionproxy.service.NotionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("notion")
@CrossOrigin
public class NotionController {

    private final NotionService notionService;

    public NotionController(NotionService notionService) {
        this.notionService = notionService;
    }


    @GetMapping
    public ResponseEntity<String> getNotion() {
        notionService.getResponsiblePerson();
        return ResponseEntity.ok("Notion proxy available" );
    }

//    @GetMapping
//    public ResponseEntity<StudentDto> getStudentIds() {
//
//    }

}
