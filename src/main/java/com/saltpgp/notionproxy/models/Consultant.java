package com.saltpgp.notionproxy.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.context.annotation.Lazy;

import java.util.List;
import java.util.UUID;


public record Consultant(String name, UUID uuid,  @JsonIgnore List<ResponsiblePerson> responsiblePersonList) {
}
