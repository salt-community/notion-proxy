package com.saltpgp.notionproxy.staff.dtos;

import com.saltpgp.notionproxy.staff.models.Staff;
import com.saltpgp.notionproxy.staff.models.StaffDev;

import java.util.UUID;

public record StaffConsultantDto(String name, String email, UUID devId) {

    public static StaffConsultantDto fromModel(StaffDev dev) {
        return new StaffConsultantDto(dev.getName(), dev.getEmail(), dev.getId());
    }
}
