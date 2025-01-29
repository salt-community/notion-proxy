package com.saltpgp.notionproxy.modules.staff.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saltpgp.notionproxy.modules.developer.model.Developer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class Staff {
    private String name, email;
    private UUID id;
    private String role;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static JsonNode toJsonNode(Staff staff) {
        return objectMapper.convertValue(staff, JsonNode.class);
    }

    public static JsonNode toJsonNode(List<Staff> staffList) {
        Map<String, Object> result = Map.of("staffList", staffList);
        return objectMapper.convertValue(result, JsonNode.class);
    }

    public static Staff fromJson(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, Staff.class);
    }

    public static List<Staff> fromJsonList(String json) throws JsonProcessingException {
        return objectMapper.convertValue(objectMapper.readTree(json).get("staffList") ,new TypeReference<List<Staff>>() {});
    }
}
