package com.saltpgp.notionproxy.dtos.outgoing;

import com.saltpgp.notionproxy.models.Consultant;

import java.util.List;
import java.util.UUID;

public record Consultant2Dto(String name, UUID id) {

    public static Consultant2Dto fromModel(Consultant consultant) {
        return new Consultant2Dto(
                consultant.name(),
                consultant.uuid()
        );
    }

}
