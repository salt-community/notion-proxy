package com.saltpgp.notionproxy.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.saltpgp.notionproxy.exceptions.NotionException;
import com.saltpgp.notionproxy.models.Consultant;
import com.saltpgp.notionproxy.models.Developer;
import com.saltpgp.notionproxy.models.ResponsiblePerson;
import com.saltpgp.notionproxy.models.Score;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class NotionService {

    private final RestClient restClient;

    @Value("${NOTION_API_KEY}")
    private String API_KEY;

    @Value("${DATABASE_ID}")
    private String DATABASE_ID;

    @Value("${NOTION_VERSION}")
    private String NOTION_VERSION;

    @Value("${SCORE_DATABASE_ID}")
    private String SCORE_DATABASE_ID;

    public NotionService() {
        restClient = RestClient.builder().baseUrl("https://api.notion.com/v1").build();
    }

    public Consultant getResponsiblePersonNameByUserId(UUID id, boolean includeNull) throws NotionException {
        JsonNode response = restClient
                .get()
                .uri(String.format("/pages/%s", id))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + API_KEY)
                .header("Notion-Version", NOTION_VERSION)
                .retrieve()
                .body(JsonNode.class);

        if (response == null) {
            throw new NotionException();
        }

        if (response.get("properties").get("Name").get("title").get(0) == null) {
            return null;
        }

        List<ResponsiblePerson> responsiblePersonList = getResponsiblePersonsFromResponse(response, includeNull);

        return new Consultant(
                response.get("properties").get("Name").get("title").get(0).get("plain_text").asText(),
                UUID.fromString(response.get("id").asText()),
                responsiblePersonList);
    }

    private List<ResponsiblePerson> getResponsiblePersonsFromResponse(JsonNode response, boolean includeNull) {
        List<ResponsiblePerson> responsiblePersonList = new ArrayList<>();
        if (response.get("properties").get("Responsible").get("people").get(0) != null) {
            response.get("properties").get("Responsible").get("people").elements().forEachRemaining(element2 -> {
                String name = null;
                if (element2.get("name") != null) {
                    name = element2.get("name").asText();
                } else if (!includeNull) {
                    return;
                }
                ResponsiblePerson responsiblePerson = new ResponsiblePerson(
                        name,
                        UUID.fromString(element2.get("id").asText()));
                responsiblePersonList.add(responsiblePerson);
            });
        }
        return responsiblePersonList;
    }

    public List<Consultant> getConsultants(boolean includeEmpty, boolean includeNull) throws NotionException {
        List<Consultant> allConsultants = new ArrayList<>();
        String nextCursor = null;
        boolean hasMore = true;

        while (hasMore) {
            JsonNode response = restClient
                    .post()
                    .uri(String.format("/databases/%s/query", DATABASE_ID))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Notion-Version", NOTION_VERSION)
                    .body(createQueryRequestBody(nextCursor))
                    .retrieve()
                    .body(JsonNode.class);

            if (response == null) {
                throw new NotionException();
            }

            response.get("results").elements().forEachRemaining(element -> {
                if (element.get("properties").get("Name").get("title").get(0) == null) return;

                List<ResponsiblePerson> responsiblePersonList = getResponsiblePersonsFromResponse(element, includeNull);
                if (responsiblePersonList.isEmpty() && !includeEmpty) {
                    return;
                }

                Consultant consultant = new Consultant(
                        element.get("properties").get("Name").get("title").get(0).get("plain_text").asText(),
                        UUID.fromString(element.get("id").asText()),
                        responsiblePersonList);

                allConsultants.add(consultant);
            });

            nextCursor = response.get("next_cursor").asText();
            hasMore = response.get("has_more").asBoolean();
        }

        return allConsultants;
    }

    private JsonNode createQueryRequestBody(String nextCursor) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode body = objectMapper.createObjectNode();

        if (nextCursor != null) {
            body.put("start_cursor", nextCursor);
        }
        return body;
    }


    public List<Developer> getSalties() throws NotionException {
        List<Developer> allSalties = new ArrayList<>();
        String nextCursor = null;
        boolean hasMore = true;

        while (hasMore) {
            JsonNode response = restClient
                    .post()
                    .uri(String.format("/databases/%s/query", DATABASE_ID))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Notion-Version", NOTION_VERSION)
                    .body(createQueryRequestBody(nextCursor))
                    .retrieve()
                    .body(JsonNode.class);

            if (response == null) {
                throw new NotionException();
            }

            response.get("results").elements().forEachRemaining(element -> {
                if (element.get("properties").get("Name").get("title").get(0) == null) return;

                Developer saltie = new Developer(
                        element.get("properties").get("Name").get("title").get(0).get("plain_text").asText(),
                        UUID.fromString(element.get("id").asText()),
                        "",
                        "",
                        "",
                        Collections.emptyList());

                allSalties.add(saltie);
            });

            nextCursor = response.get("next_cursor").asText();
            hasMore = response.get("has_more").asBoolean();
        }
        return allSalties;
    }

    public List<Score> getDeveloperScores(UUID id) throws NotionException {
        List<Score> allScores = new ArrayList<>();
        String nextCursor = null;
        boolean hasMore = true;
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode relationNode = objectMapper.createObjectNode();
        relationNode.put("contains", String.valueOf(id));

        ObjectNode filterNode = objectMapper.createObjectNode();
        filterNode.put("property", "💽 Developer");
        filterNode.set("relation", relationNode);

        ObjectNode bodyNode = objectMapper.createObjectNode();
        bodyNode.set("filter", filterNode);
        while (hasMore) {

            if (nextCursor != null) {
                bodyNode.put("start_cursor", nextCursor);
            }
            JsonNode response = restClient
                    .post()
                    .uri(String.format("/databases/%s/query", SCORE_DATABASE_ID))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Notion-Version", NOTION_VERSION)
                    .body(bodyNode)
                    .retrieve()
                    .body(JsonNode.class);

            if (response == null) {
                throw new NotionException();
            }
            if (response.get("results").isEmpty()) {
                throw new NotionException();
            }
            response.get("results").elements().forEachRemaining(element -> {
                System.out.println(element.get("properties").get("Score").get("number").asInt());
                System.out.println(element.get("properties").get("Name").get("title").get(0).get("plain_text").asText());
                System.out.println(element.get("id"));
                System.out.println(bodyNode);

                //if (element.get("properties").get("Score").get("number") == null) return;
                if (element.get("properties").get("Score") == null) return;

                List<String> categories = new ArrayList<>();
                if (element.get("properties").get("Categories") != null) {
                    element.get("properties").get("Categories").get("multi_select").forEach(category ->
                            categories.add(category.get("name").asText()));
                }

                Score score = new Score(
                        element.get("properties").get("Name").get("title").get(0).get("plain_text").asText(),
                        element.get("properties").get("Score").get("number").asInt(),
                        categories
                );

                allScores.add(score);
            });

            nextCursor = response.get("next_cursor").asText();
            hasMore = response.get("has_more").asBoolean();
        }
        return allScores;
    }

}

