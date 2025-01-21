package com.saltpgp.notionproxy.developer.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class Responsible {
    private String name;
    private UUID id;
    private String email;
}
//Test