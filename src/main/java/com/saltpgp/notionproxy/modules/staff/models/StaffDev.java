package com.saltpgp.notionproxy.modules.staff.models;

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
public class StaffDev {
    private String name, email;
    private UUID id;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static JsonNode toJsonNode(List<StaffDev> staffDevs) {
        Map<String, Object> result = Map.of("staffDevList", staffDevs);
        return objectMapper.convertValue(result, JsonNode.class);
    }

    public static List<StaffDev> fromJsonList(String json) throws JsonProcessingException {
        return objectMapper.convertValue(objectMapper.readTree(json).get("staffDevList") ,new TypeReference<List<StaffDev>>() {});
    }
}
