package com.saltpgp.notionproxy.modules.staff.service;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.UUID;
import static com.saltpgp.notionproxy.modules.staff.service.StaffProperty.*;

class StaffMapper {

    public static JsonNode getStaffPerson(JsonNode element) {
        return element.get(Results.PROPERTIES).get(Properties.PERSON).get(Person.PEOPLE).get(0);
    }

    public static UUID getStaffId(JsonNode person) {
        return UUID.fromString(person.get(People.ID).asText());
    }

    public static String getStaffEmail(JsonNode person) {
        return person.get(People.PERSON).get(Person.EMAIL).asText();
    }

    public static String getStaffName(JsonNode person) {
        return person.get(Person.NAME).asText();
    }

    public static String getStaffRole(JsonNode element) {
        return element.get(Results.PROPERTIES).get(Properties.GUILD).get(Guild.MULTI_SELECT).get(0).get(MultiSelect.NAME).asText();
    }

    public static String getConsultantEmail(JsonNode node) {
        try {
            return node.get(Results.PROPERTIES).get(Properties.EMAIL).get(Email.EMAIL).asText();
        } catch (NullPointerException e) {
            return NULL;
        }
    }

    public static UUID getConsultantId(JsonNode page) {
        return UUID.fromString(page.get(Results.ID).asText());
    }

    public static String getConsultantName(JsonNode page) {
        return page.get(Results.PROPERTIES).get(Properties.NAME).get(Name.TITLE).get(0).get(Title.PLAIN_TEXT).asText();
    }
}
