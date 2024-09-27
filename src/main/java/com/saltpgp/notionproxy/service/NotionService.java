package com.saltpgp.notionproxy.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.saltpgp.notionproxy.exceptions.NotionException;
import com.saltpgp.notionproxy.models.Consultant;
import com.saltpgp.notionproxy.models.Developer;
import com.saltpgp.notionproxy.models.ResponsiblePerson;
import com.saltpgp.notionproxy.models.Score;
import com.saltpgp.notionproxy.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.*;

@Service
public class NotionService {

    public RestClient restClient;

    @Value("${NOTION_API_KEY}")
    private String API_KEY;

    @Value("${DATABASE_ID}")
    private String DATABASE_ID;

    @Value("${NOTION_VERSION}")
    private String NOTION_VERSION;

    @Value("${SCORE_DATABASE_ID}")
    private String SCORE_DATABASE_ID;

    @Value("${PEOPLE_AND_TALENT}")
    private String PEOPLE_AND_TALENT;

    @Autowired
    @Lazy
    private NotionService self;


    public NotionService(RestClient.Builder builder) {
        this.restClient = builder.baseUrl("https://api.notion.com/v1").build();
    }

    public Consultant getConsultantById(UUID id, boolean includeNull) throws NotionException {
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

                List<String> ptPeople = List.of(StringUtils.normalizeSwedishAlphabet(PEOPLE_AND_TALENT).split(","));
                if (!ptPeople.contains(name)) {
                    return;
                }

                String email = null;
                if (element2.get("person") != null) {
                    if (element2.get("person").get("email") != null) {
                        email = element2.get("person").get("email").asText();
                    } else if (!includeNull) {
                        return;
                    }
                }
                List<Consultant> consultants = new ArrayList<>();
                ResponsiblePerson responsiblePerson = new ResponsiblePerson(
                        name,
                        UUID.fromString(element2.get("id").asText()),
                        email,
                        consultants);
                responsiblePersonList.add(responsiblePerson);
            });
        }
        return responsiblePersonList;
    }

    public Set<ResponsiblePerson> getAllResponsiblePeople(boolean includeNull, boolean includeConsultants) throws NotionException {
        List<Consultant> consultants = getAllConsultants(true, includeNull);
        Set<ResponsiblePerson> responsiblePersonList = new HashSet<>();
        consultants.forEach(c -> responsiblePersonList.addAll(c.responsiblePersonList()));

        if(includeConsultants){
            responsiblePersonList.forEach(responsiblePerson -> {
                List<Consultant> consultants1 = new ArrayList<>();
                consultants.forEach(consultant -> {
                    System.out.println(consultant.name());
                     consultant.responsiblePersonList().forEach(responsiblePerson1 -> {

                        if(responsiblePerson1.id().equals(responsiblePerson.id())){
                            consultants1.add(consultant);
                        }
                    });
                });
                consultants1.forEach(consultant -> responsiblePerson.consultants().add(consultant));
            });
        }
        return responsiblePersonList;
    }

    public ResponsiblePerson getResponsiblePersonById(UUID id, boolean includeNull, boolean includeConsultants) throws NotionException {
        Set<ResponsiblePerson> responsiblePersonList = getAllResponsiblePeople(includeNull, includeConsultants);
        return responsiblePersonList.stream()
                .filter(responsiblePerson -> responsiblePerson.id().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Consultant> getAllConsultants(boolean includeEmpty, boolean includeNull) throws NotionException {
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

    @Lazy
    @Cacheable(value = "saltiesInformation")
    public List<Developer> getAllDevelopers() throws NotionException {
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
                String githubUrl = element.get("properties").get("GitHub").get("url").asText().equals("null") ? null
                        : element.get("properties").get("GitHub").get("url").asText();

                String githubImageUrl = githubUrl == null ? null : githubUrl + ".png";

                String email = element.get("properties").get("Private Email").get("email").asText().equals("null") ? null
                        : element.get("properties").get("Private Email").get("email").asText();

                Developer saltie = new Developer(
                        element.get("properties").get("Name").get("title").get(0).get("plain_text").asText(),
                        UUID.fromString(element.get("id").asText()),
                        githubUrl,
                        githubImageUrl,
                        email,
                        Collections.emptyList());

                allSalties.add(saltie);
            });

            nextCursor = response.get("next_cursor").asText();
            hasMore = response.get("has_more").asBoolean();
        }
        return allSalties;
    }

    @Cacheable(value = "developerScoreCard", key = "#id")
    public Developer getDeveloperByIdWithScore(UUID id) throws NotionException {
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

        List<Developer> allSalties = self.getAllDevelopers();

        Developer developer = allSalties.stream()
                .filter(dev -> dev.getId().equals(id))
                .findFirst()
                .orElseThrow(NotionException::new);

        return new Developer(
                developer.getName(),
                developer.getId(),
                developer.getGithubUrl(),
                developer.getGithubImageUrl(),
                developer.getEmail(),
                allScores
        );
    }


}
