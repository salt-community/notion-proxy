package com.saltpgp.notionproxy.modules.developer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Developer {

    private String name;
    private UUID id;
    private String githubUrl;
    private String githubImageUrl;
    private String email;
    private String status;
    private String totalScore;
    private List<Responsible> responsible;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static JsonNode toJsonNode(Developer developer) {
        return objectMapper.convertValue(developer, JsonNode.class);
    }

    public static JsonNode toJsonNode(List<Developer> developerList) {
        Map<String, Object> result = Map.of("developerList", developerList);
        return objectMapper.convertValue(result, JsonNode.class);
    }

    public static Developer fromJson(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, Developer.class);
    }

    public static List<Developer> fromJsonList(String json) throws JsonProcessingException {
        return objectMapper.convertValue(objectMapper.readTree(json).get("developerList") ,new TypeReference<List<Developer>>() {});
    }
}
