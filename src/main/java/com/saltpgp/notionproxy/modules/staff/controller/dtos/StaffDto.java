package com.saltpgp.notionproxy.modules.staff.controller.dtos;

import com.saltpgp.notionproxy.modules.staff.model.Staff;

import java.util.UUID;

public record StaffDto(String name, String email, UUID staffId, String role) {
    public static StaffDto fromModel(Staff staff) {
        return new StaffDto(staff.getName(), staff.getEmail(), staff.getId(), staff.getRole());
    }
}
