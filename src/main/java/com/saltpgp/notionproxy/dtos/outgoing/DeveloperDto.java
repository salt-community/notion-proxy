package com.saltpgp.notionproxy.dtos.outgoing;

import com.saltpgp.notionproxy.models.Developer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record DeveloperDto(String name, UUID id, String githubUrl,String githubImageUrl, String email, String status, String totalScore, List<ScoreDto>scores) {

    public static DeveloperDto fromModel(Developer developer) {
        return new DeveloperDto(
                developer.getName(),
                developer.getId(),
                developer.getGithubUrl(),
                developer.getGithubImageUrl(),
                developer.getEmail(),
                developer.getStatus(),
                developer.getTotalScore(),
                ScoreDto.fromModel(developer.getScores())
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
                    developer.getTotalScore(),
                    ScoreDto.fromModel(developer.getScores())));
        }
        return developerDtoList;
    }

}
