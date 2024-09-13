package com.saltpgp.notionproxy.dtos.outgoing;

import com.saltpgp.notionproxy.models.Developer;

import java.util.List;
import java.util.UUID;

public record DeveloperDto(String name, UUID id, String githubUrl, String email, List<ScoreDto>scores) {
    public static DeveloperDto fromModel(Developer developer) {
        return new DeveloperDto(
                developer.getName(),
                developer.getId(),
                developer.getGithubUrl(),
                developer.getEmail(),
                ScoreDto.fromModel(developer.getScores())
        );
    }

}
