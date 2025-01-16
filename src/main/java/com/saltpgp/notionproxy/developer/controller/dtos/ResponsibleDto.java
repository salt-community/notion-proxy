package com.saltpgp.notionproxy.developer.controller.dtos;

import com.saltpgp.notionproxy.developer.model.Responsible;

import java.util.UUID;

public record ResponsibleDto( String name, UUID id, String email) {
    public static ResponsibleDto fromModel(Responsible responsible) {
        return new ResponsibleDto(
                responsible.getName(),
                responsible.getId(),
                responsible.getEmail()
        );
    }
}
