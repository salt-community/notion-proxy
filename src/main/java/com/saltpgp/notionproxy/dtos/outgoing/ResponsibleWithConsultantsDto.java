package com.saltpgp.notionproxy.dtos.outgoing;

import com.saltpgp.notionproxy.models.ResponsiblePerson;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record ResponsibleWithConsultantsDto(String name, UUID id, String email, List<BasicConsultantDto> consultants) {

    public static List<ResponsibleWithConsultantsDto> fromModelSet(Set<ResponsiblePerson> responsiblePerson) {
        List<ResponsibleWithConsultantsDto> responsibleWithConsultantsDtos = new ArrayList<>();
        for(ResponsiblePerson person : responsiblePerson) {
           responsibleWithConsultantsDtos.add(new ResponsibleWithConsultantsDto(person.name(),person.id(),person.email(),person.consultants().stream().map(consultant -> BasicConsultantDto.fromModel(consultant)).toList()));
        }
        return responsibleWithConsultantsDtos;
    }

    public static List<ResponsibleWithConsultantsDto> fromModelList(List<ResponsiblePerson> responsiblePerson) {
        List<ResponsibleWithConsultantsDto> responsibleWithConsultantsDtos = new ArrayList<>();
        for(ResponsiblePerson person : responsiblePerson) {
            responsibleWithConsultantsDtos.add(new ResponsibleWithConsultantsDto(person.name(),person.id(),person.email(),person.consultants().stream().map(consultant -> BasicConsultantDto.fromModel(consultant)).toList()));
        }
        return responsibleWithConsultantsDtos;
    }

    public static ResponsibleWithConsultantsDto fromModel(ResponsiblePerson responsiblePerson) {
       return new ResponsibleWithConsultantsDto(
               responsiblePerson.name(),
               responsiblePerson.id(),
               responsiblePerson.email(),
               responsiblePerson.consultants().stream().map(consultant -> BasicConsultantDto.fromModel(consultant)).toList());
    }

}
