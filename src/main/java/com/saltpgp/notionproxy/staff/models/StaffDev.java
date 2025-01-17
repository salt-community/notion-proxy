package com.saltpgp.notionproxy.staff.models;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StaffDev {
    private String name, email;
    private UUID id;
}
