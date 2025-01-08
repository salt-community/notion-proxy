package com.saltpgp.notionproxy.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class Score {
    private String name;
    private int score;
    private List<String> categories;
    private String comment;

}
