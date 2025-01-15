package com.saltpgp.notionproxy.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class Developer {

    private String name;
    private UUID id;
    private String githubUrl;
    private String githubImageUrl;
    private String email;
    private String status;
    private List<Score> scores;


    public static Developer addScore(Developer developer, List<Score> scores) {
        return new Developer(
                developer.getName(),
                developer.getId(),
                developer.getGithubUrl(),
                developer.getGithubImageUrl(),
                developer.getEmail(),
                developer.getStatus(),
                scores
        );

    }
}
