package com.saltpgp.notionproxy.modules.idcard.service;

import com.fasterxml.jackson.databind.JsonNode;
import static com.saltpgp.notionproxy.modules.idcard.service.IdCardProperty.*;

class IdCardMapper {
    public static String getGitHub(JsonNode properties) {
        return properties.get(Properties.GITHUB).get(GitHub.URL).asText();
    }

    public static String getEmail(JsonNode properties) {
        return properties.get(Properties.EMAIL).get(Email.EMAIL).asText();
    }

    public static String getCourse(JsonNode properties) {
        return properties.get(Properties.COURSE).get(Course.SELECT).get(Select.NAME).asText();
    }

    public static String getText(JsonNode properties) {
        return properties.get(Properties.NAME).get(Name.TITLE).get(0).get(Title.PLAIN_TEXT).asText();
    }

    public static String getId(JsonNode page) {
        return page.get(Results.ID).asText();
    }
}
