package com.saltpgp.notionproxy.models;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Developer {

    private String name;
    private String githubUrl;
    private String email;
    private List<Score> scores;
}
