package com.saltpgp.notionproxy.dtos.outgoing;

import com.saltpgp.notionproxy.models.ResponsiblePerson;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record ResponsiblePerson2Dto(String name, UUID id, String email, List<Consultant2Dto> consultants) {

    public static List<ResponsiblePerson2Dto> fromModel(Set<ResponsiblePerson> responsiblePerson) {
        List<ResponsiblePerson2Dto> responsiblePerson2Dtos = new ArrayList<>();
        for(ResponsiblePerson person : responsiblePerson) {
           responsiblePerson2Dtos.add(new ResponsiblePerson2Dto(person.name(),person.id(),person.email(),person.consultants().stream().map(consultant -> Consultant2Dto.fromModel(consultant)).toList()));
        }
        return responsiblePerson2Dtos;
    }

}
