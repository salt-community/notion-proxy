package com.saltpgp.notionproxy.assignment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor

public class Assignment {
    private String id;
    private String name;
    private int score;
    private List<String> categories;
    private String comment;
}
