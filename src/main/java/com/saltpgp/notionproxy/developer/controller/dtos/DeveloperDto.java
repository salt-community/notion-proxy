package com.saltpgp.notionproxy.developer.controller.dtos;

import com.saltpgp.notionproxy.developer.model.Developer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record DeveloperDto(String name, UUID id, String status, String email, String githubUrl, String githubImageUrl, String totalScore) {

    public static DeveloperDto fromModel(Developer developer) {
        return new DeveloperDto(
                developer.getName(),
                developer.getId(),
                developer.getGithubUrl(),
                developer.getGithubImageUrl(),
                developer.getEmail(),
                developer.getStatus(),
                developer.getTotalScore()
        );
    }

    public static List<DeveloperDto> fromModelList(List<Developer> developers) {
        List<DeveloperDto> developerDtoList = new ArrayList<>();
        for (Developer developer : developers) {
            developerDtoList.add(new DeveloperDto(
                    developer.getName(),
                    developer.getId(),
                    developer.getGithubUrl(),
                    developer.getGithubImageUrl(),
                    developer.getEmail(),
                    developer.getStatus(),
                    developer.getTotalScore()));
        }
        return developerDtoList;
    }

}
