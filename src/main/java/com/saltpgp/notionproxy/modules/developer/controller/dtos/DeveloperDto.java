package com.saltpgp.notionproxy.modules.developer.controller.dtos;

import com.saltpgp.notionproxy.modules.developer.model.Developer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record DeveloperDto(String name, UUID id, String status, String email, String githubUrl, String githubImageUrl, String totalScore, List<ResponsibleDto> responsibles) {

    public static DeveloperDto fromModel(Developer developer) {
        return new DeveloperDto(
                developer.getName(),
                developer.getId(),
                developer.getStatus(),
                developer.getEmail(),
                developer.getGithubUrl(),
                developer.getGithubImageUrl(),
                developer.getTotalScore(),
                ResponsibleDto.fromModelList(developer.getResponsible())
        );
    }

    public static List<DeveloperDto> fromModelList(List<Developer> developers) {
        List<DeveloperDto> developerDtoList = new ArrayList<>();
        for (Developer developer : developers) {
            developerDtoList.add(fromModel(developer));
        }
        return developerDtoList;
    }

}
