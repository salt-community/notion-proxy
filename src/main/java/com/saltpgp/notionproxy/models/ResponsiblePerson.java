package com.saltpgp.notionproxy.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;


public record ResponsiblePerson(String name, UUID id, String email, List<Consultant> consultants) {
}
