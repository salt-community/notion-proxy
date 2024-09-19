package com.saltpgp.notionproxy.dtos.outgoing;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.saltpgp.notionproxy.models.Consultant;
import com.saltpgp.notionproxy.models.ResponsiblePerson;
import org.springframework.context.annotation.Lazy;

import java.util.List;
import java.util.UUID;


public record ResponsiblePersonDto(String name, UUID id, String email) {

    static ResponsiblePersonDto fromModel(ResponsiblePerson responsiblePerson) {
        return new ResponsiblePersonDto(
                responsiblePerson.name(),
                responsiblePerson.id(),
                responsiblePerson.email()
        );
    }

}
