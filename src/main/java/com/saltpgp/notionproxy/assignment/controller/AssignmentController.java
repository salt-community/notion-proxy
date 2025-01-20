package com.saltpgp.notionproxy.assignment.controller;

import com.saltpgp.notionproxy.assignment.model.Assignment;
import com.saltpgp.notionproxy.assignment.service.AssignmentService;
import com.saltpgp.notionproxy.exceptions.NotionException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Assignment>> getAssignmentsByUserId(@PathVariable("userId") UUID userId) throws NotionException {
        List<Assignment> assignments = assignmentService.getDeveloperByIdWithScore(userId);
        return ResponseEntity.ok(assignments);
    }
}
