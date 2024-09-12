package com.saltpgp.notionproxy.dtos.outgoing;

import com.saltpgp.notionproxy.models.Developer;

import java.util.List;

public record DeveloperDto(String name, String githubUrl, String email, List<ScoreDto>scores) {
    public static DeveloperDto fromModel(Developer developer) {
        return new DeveloperDto(
                developer.getName(),
                developer.getGithubUrl(),
                developer.getEmail(),
                ScoreDto.fromModel(developer.getScores())
        );
    }

}
