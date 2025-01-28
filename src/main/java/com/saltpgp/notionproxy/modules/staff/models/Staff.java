package com.saltpgp.notionproxy.modules.staff.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class Staff {
    private String name, email;
    private UUID id;
    private String role;
}
