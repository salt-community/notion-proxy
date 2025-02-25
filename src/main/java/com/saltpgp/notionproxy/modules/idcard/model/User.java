package com.saltpgp.notionproxy.modules.idcard.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {
    private String uuid, name, course, email, gitHub;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static JsonNode toJsonNode(User user) {
        return objectMapper.convertValue(user, JsonNode.class);
    }

    public static User fromJson(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, User.class);
    }

}
