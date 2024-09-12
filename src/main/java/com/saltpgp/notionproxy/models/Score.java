package com.saltpgp.notionproxy.models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Score {
    private String name;
    private int score;
    private List<String> categories;

}
