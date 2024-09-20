package com.saltpgp.notionproxy.dtos.outgoing;

import com.saltpgp.notionproxy.models.Consultant;

import java.util.List;
import java.util.UUID;


public record ConsultantWithResponsibleDto(String name, UUID id, List<BasicResponsiblePersonDto> responsiblePersonList) {

    public static ConsultantWithResponsibleDto fromModel(Consultant consultant) {
        return new ConsultantWithResponsibleDto(
                consultant.name(),
                consultant.uuid(),
                consultant.responsiblePersonList().stream().map(BasicResponsiblePersonDto::fromModel).toList()
        );
    }

}
