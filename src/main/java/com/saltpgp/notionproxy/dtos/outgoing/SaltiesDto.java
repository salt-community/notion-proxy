package com.saltpgp.notionproxy.dtos.outgoing;

import com.saltpgp.notionproxy.models.Developer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record SaltiesDto(String name, UUID id) {

    public static List<SaltiesDto> fromModel(List<Developer> developers) {
        List<SaltiesDto> salties = new ArrayList<>();
        for (Developer developer : developers) {
            salties.add(new SaltiesDto(developer.getName(), developer.getId()));
        }
        return salties;
    }

}
