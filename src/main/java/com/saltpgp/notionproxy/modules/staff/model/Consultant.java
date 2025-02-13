package com.saltpgp.notionproxy.modules.staff.model;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Consultant {
    private String name, email;
    private UUID id;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static JsonNode toJsonNode(List<Consultant> staffDevs) {
        Map<String, Object> result = Map.of("consultantList", staffDevs);
        return objectMapper.convertValue(result, JsonNode.class);
    }

    public static List<Consultant> fromJsonList(String json) throws JsonProcessingException {
        return objectMapper.convertValue(objectMapper.readTree(json).get("consultantList") ,new TypeReference<List<Consultant>>() {});
    }
}
