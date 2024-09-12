package com.saltpgp.notionproxy.dtos.outgoing;

import com.saltpgp.notionproxy.models.ResponsiblePerson;

import java.util.UUID;

public record ResponsiblePersonDto(String name, UUID id) {

    static ResponsiblePersonDto fromModel(ResponsiblePerson responsiblePerson) {
        return new ResponsiblePersonDto(
                responsiblePerson.name(),
                responsiblePerson.id()
        );
    }

}
