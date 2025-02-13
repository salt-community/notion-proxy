package com.saltpgp.notionproxy.modules.staff.service;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.UUID;

class StaffMapper {

    public static UUID getStaffId(JsonNode person) {
        return UUID.fromString(person.get("id").asText());
    }

    public static String getStaffEmail(JsonNode person) {
        return person.get("person").get("email").asText();
    }

    public static String getStaffName(JsonNode person) {
        return person.get("name").asText();
    }

    public static String getStaffRole(JsonNode element) {
        return element.get("properties").get("Guild").get("multi_select").get(0).get("name").asText();
    }

    public static JsonNode getStaffPerson(JsonNode element) {
        return element.get("properties").get("Person").get("people").get(0);
    }

    public static String getConsultantEmail(JsonNode node) {
        String output = "No email found";
        try {
            return node.get("properties").get("Email").get("email").asText();
        } catch (NullPointerException e) {
            return output;
        }
    }

    public static UUID getConsultantId(JsonNode page) {
        return UUID.fromString(page.get("id").asText());
    }

    public static String getConsultantName(JsonNode page) {
        return page.get("properties").get("Name").get("title").get(0).get("plain_text").asText();
    }
}
