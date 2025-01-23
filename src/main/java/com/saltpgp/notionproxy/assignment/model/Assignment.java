package com.saltpgp.notionproxy.assignment.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String toJson(Assignment assignment) throws JsonProcessingException {
        return objectMapper.writeValueAsString(assignment);
    }

    public static Assignment fromJson(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, Assignment.class);
    }

    public static String toJsonList(List<Assignment> assignmentList) throws JsonProcessingException {
        return objectMapper.writeValueAsString(assignmentList);
    }

    public static List<Assignment> fromJsonList(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, new TypeReference<List<Assignment>>() {});
    }
}
