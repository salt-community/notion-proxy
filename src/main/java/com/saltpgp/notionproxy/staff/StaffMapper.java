package com.saltpgp.notionproxy.staff;

import com.fasterxml.jackson.databind.JsonNode;

public class StaffMapper {

    public static String getDevEmail(JsonNode node) {
        String output = "No email found";
        try {
            return node.get("properties").get("Email").get("email").asText();
        } catch (NullPointerException e) {
            return output;
        }
    }
}
