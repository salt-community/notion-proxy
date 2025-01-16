package com.saltpgp.notionproxy.developer.model;

import com.saltpgp.notionproxy.models.ResponsiblePerson;
import com.saltpgp.notionproxy.models.Score;
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
    private String totalScore;
    private List<Score> scores;
    private List<ResponsiblePerson> responsiblePersonList;

}
