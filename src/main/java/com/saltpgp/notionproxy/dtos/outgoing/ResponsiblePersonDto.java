package com.saltpgp.notionproxy.dtos.outgoing;

import com.saltpgp.notionproxy.models.Consultant;
import com.saltpgp.notionproxy.models.ResponsiblePerson;
import org.springframework.context.annotation.Lazy;

import java.util.List;
import java.util.UUID;

@Lazy
public record ResponsiblePersonDto(String name, UUID id, String email, List<ConsultantDto> consultants) {

    static ResponsiblePersonDto fromModel(ResponsiblePerson responsiblePerson) {
        return new ResponsiblePersonDto(
                responsiblePerson.getName(),
                responsiblePerson.getId(),
                responsiblePerson.getEmail(),
                responsiblePerson.getConsultants().stream().map(ConsultantDto::fromModel).toList()

        );
    }

}
