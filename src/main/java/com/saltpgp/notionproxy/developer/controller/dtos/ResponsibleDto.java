package com.saltpgp.notionproxy.developer.controller.dtos;

import com.saltpgp.notionproxy.developer.model.Responsible;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record ResponsibleDto( String name, UUID id, String email) {
    public static List<ResponsibleDto> fromModelList(List<Responsible> responsibles) {
        List<ResponsibleDto> responsibleDtoList = new ArrayList<>();
        for (Responsible responsible : responsibles) {
            responsibleDtoList.add(new ResponsibleDto(
                    responsible.getName(),
                    responsible.getId(),
                    responsible.getEmail()
            ));
        }
        return responsibleDtoList;
    }
}
