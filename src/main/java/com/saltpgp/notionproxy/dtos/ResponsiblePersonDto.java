package com.saltpgp.notionproxy.dtos;

import com.saltpgp.notionproxy.models.ResponsiblePerson;

import java.util.UUID;

public record ResponsiblePersonDto(String name, UUID uuid) {

    static ResponsiblePersonDto fromModel(ResponsiblePerson responsiblePerson) {
        return new ResponsiblePersonDto(
                responsiblePerson.name(),
                responsiblePerson.id()
        );
    }

}
