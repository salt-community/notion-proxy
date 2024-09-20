package com.saltpgp.notionproxy.dtos.outgoing;

import com.saltpgp.notionproxy.models.Consultant;

import java.util.UUID;

public record BasicConsultantDto(String name, UUID id) {

    public static BasicConsultantDto fromModel(Consultant consultant) {
        return new BasicConsultantDto(
                consultant.name(),
                consultant.uuid()
        );
    }

}
