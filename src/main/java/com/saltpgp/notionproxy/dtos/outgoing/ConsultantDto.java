package com.saltpgp.notionproxy.dtos.outgoing;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saltpgp.notionproxy.models.Consultant;

import java.util.List;
import java.util.UUID;

public record ConsultantDto(String name, UUID id,  @JsonIgnore List<ResponsiblePersonDto> responsiblePersonList) {

    public static ConsultantDto fromModel(Consultant consultant) {
        return new ConsultantDto(
                consultant.name(),
                consultant.uuid(),
                consultant.responsiblePersonList().stream().map(ResponsiblePersonDto::fromModel).toList()
        );
    }

}
