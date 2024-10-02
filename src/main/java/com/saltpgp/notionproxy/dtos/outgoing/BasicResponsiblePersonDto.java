package com.saltpgp.notionproxy.dtos.outgoing;

import com.saltpgp.notionproxy.models.ResponsiblePerson;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;


public record BasicResponsiblePersonDto(String name, UUID id, String email) {

    public static BasicResponsiblePersonDto fromModel(ResponsiblePerson responsiblePerson) {
        return new BasicResponsiblePersonDto(
                responsiblePerson.name(),
                responsiblePerson.id(),
                responsiblePerson.email()
        );
    }

    public static List<BasicResponsiblePersonDto> fromModelSet(Set<ResponsiblePerson> responsiblePerson) {
        List<BasicResponsiblePersonDto> basicResponsiblePersonDtos = new ArrayList<>();
        for(ResponsiblePerson person : responsiblePerson) {
            basicResponsiblePersonDtos.add(new BasicResponsiblePersonDto(person.name(),person.id(),person.email()));
        }
        return basicResponsiblePersonDtos;
    }

    public static List<BasicResponsiblePersonDto> fromModelList(List<ResponsiblePerson> responsiblePerson) {
        List<BasicResponsiblePersonDto> basicResponsiblePersonDtos = new ArrayList<>();
        for(ResponsiblePerson person : responsiblePerson) {
            basicResponsiblePersonDtos.add(new BasicResponsiblePersonDto(person.name(),person.id(),person.email()));
        }
        return basicResponsiblePersonDtos;
    }

}
