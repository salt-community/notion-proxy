package com.saltpgp.notionproxy.staff;

import com.saltpgp.notionproxy.dtos.outgoing.ConsultantWithResponsibleDto;
import com.saltpgp.notionproxy.exceptions.NotionException;
import com.saltpgp.notionproxy.staff.dtos.StaffDto;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/staff")
@CrossOrigin
@Slf4j
public class StaffController {

    private StaffService staffService;

    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @GetMapping("")
    public ResponseEntity<List<StaffDto>> getAllStaff(
            @Parameter(description = "A filter to sort staff by role", required = false, example = "none")
            @RequestParam(required = false, defaultValue = "none") String filter) throws NotionException {
        return ResponseEntity.ok(staffService
                .getAllCore(filter)
                .stream()
                .map(StaffDto::fromModel)
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StaffDto> getStaffById(@PathVariable UUID id) throws NotionException {
        return ResponseEntity.ok(StaffDto.fromModel(staffService.getStaffById(id)));
    }
}
