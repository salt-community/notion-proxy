package com.saltpgp.notionproxy.modules.staff.controller.dtos;

import com.saltpgp.notionproxy.modules.staff.model.Consultant;

import java.util.UUID;

public record StaffConsultantDto(String name, String email, UUID devId) {

    public static StaffConsultantDto fromModel(Consultant dev) {
        return new StaffConsultantDto(dev.getName(), dev.getEmail(), dev.getId());
    }
}
