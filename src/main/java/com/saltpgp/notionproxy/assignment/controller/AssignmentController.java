package com.saltpgp.notionproxy.assignment.controller;

import com.saltpgp.notionproxy.assignment.model.Assignment;
import com.saltpgp.notionproxy.assignment.service.AssignmentService;
import com.saltpgp.notionproxy.exceptions.NotionException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @GetMapping()
    public ResponseEntity<List<Assignment>> getAssignmentsByUserId(@RequestParam(value = "developerId", required = true) UUID developerId) throws NotionException {
        List<Assignment> assignments = assignmentService.getAssignmentsFromDeveloper(developerId);
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/{developerId}")
    public ResponseEntity<Assignment> getAssignmentFomUserByAssignmentId(@RequestParam UUID assignmentId, @PathVariable String developerId) throws NotionException {
        return ResponseEntity.ok(assignmentService.getAssignmentFromDeveloper(UUID.fromString(developerId), assignmentId));
    }
}
