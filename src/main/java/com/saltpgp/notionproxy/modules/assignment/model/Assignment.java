package com.saltpgp.notionproxy.modules.assignment.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Assignment {
    private String id;
    private String name;
    private int score;
    private List<String> categories;
    private String comment;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static JsonNode toJsonNode(Assignment assignment) {
        return objectMapper.convertValue(assignment, JsonNode.class);
    }

    public static JsonNode toJsonNode(List<Assignment> assignmentList) {
        Map<String, Object> result = Map.of("assignmentList", assignmentList);
        return objectMapper.convertValue(result, JsonNode.class);
    }

    public static Assignment fromJson(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, Assignment.class);
    }

    public static List<Assignment> fromJsonList(String json) throws JsonProcessingException {
        return objectMapper.convertValue(objectMapper.readTree(json).get("assignmentList") ,new TypeReference<List<Assignment>>() {});
    }
}
