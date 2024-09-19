package com.saltpgp.notionproxy.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
public class ResponsiblePerson {
    String name;
    UUID id;
    String email;
}
