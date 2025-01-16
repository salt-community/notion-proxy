package com.saltpgp.notionproxy.staff;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/staff")
@CrossOrigin
@Slf4j
public class StaffController {

    @GetMapping("")
    public void getAllStaff() {
    }

    @GetMapping("/{id}")
    public void getStaffById(@PathVariable UUID id) {

    }
}
