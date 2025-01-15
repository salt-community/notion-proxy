package com.saltpgp.notionproxy.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.saltpgp.notionproxy.exceptions.NotionException;
import com.saltpgp.notionproxy.exceptions.NotionNotFoundException;
import com.saltpgp.notionproxy.models.Consultant;
import com.saltpgp.notionproxy.models.Developer;
import com.saltpgp.notionproxy.models.ResponsiblePerson;
import com.saltpgp.notionproxy.models.Score;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class NotionService {

    private final NotionApiService notionApiService;
    private final String DATABASE_ID;
    private final String SCORE_DATABASE_ID;
    private final String CORE_DATABASE_ID;

    public NotionService(NotionApiService notionApiService,
                         @Value("${DATABASE_ID}") String DATABASE_ID,
                         @Value("${SCORE_DATABASE_ID}") String SCORE_DATABASE_ID,
                         @Value("${CORE_DATABASE_ID}") String CORE_DATABASE_ID) {
        this.notionApiService = notionApiService;
        this.DATABASE_ID = DATABASE_ID;
        this.SCORE_DATABASE_ID = SCORE_DATABASE_ID;
        this.CORE_DATABASE_ID = CORE_DATABASE_ID;
    }

    @Lazy
    @Cacheable(value = "saltiesInformation")
    public List<Developer> getAllDevelopers(String filter) throws NotionException {
        List<Developer> allSalties = new ArrayList<>();
        String nextCursor = null;
        boolean hasMore = true;
        while (hasMore) {
            JsonNode response = notionApiService.fetchDatabase(DATABASE_ID, NotionServiceFilters.getFilterDeveloper(nextCursor, filter));

            response.get("results").elements().forEachRemaining(element -> {
                JsonNode properties = element.get("properties");
                if (properties.get("Name").get("title").get(0) == null) return;
                String githubUrl = element.get("properties").get("GitHub").get("url").asText().equals("null") ? null
                        : properties.get("GitHub").get("url").asText();

                String githubImageUrl = githubUrl == null ? null : githubUrl + ".png";
                String status = NotionServiceUtility.getDeveloperStatus(element);
                String totalScore = NotionServiceUtility.getDeveloperTotalScore(element);
                String email = properties.get("Private Email").get("email").asText().equals("null") ? null
                        : properties.get("Private Email").get("email").asText();

                Developer saltie = new Developer(
                        properties.get("Name").get("title").get(0).get("plain_text").asText(),
                        UUID.fromString(element.get("id").asText()),
                        githubUrl,
                        githubImageUrl,
                        email,
                        status,
                        totalScore,
                        Collections.emptyList());

                allSalties.add(saltie);
            });

            nextCursor = response.get("next_cursor").asText();
            hasMore = response.get("has_more").asBoolean();
            log.debug("Finished fetch of notion db. Nextcursors: {}, hasMore? = {}", nextCursor, hasMore);
            log.debug("Developers found: {}", allSalties.size());
        }
        return allSalties;
    }

    public Developer getDeveloperById(UUID id) throws NotionException {
        JsonNode response = notionApiService.fetchPage(id.toString());
        String name = response.get("properties").get("Name").get("title").get(0).get("plain_text").asText();
        String githubUrl = response.get("properties").get("GitHub").get("url").asText();
        String githubImage = githubUrl + "png";
        String email = response.get("properties").get("Private Email").get("email").asText();
        String status = NotionServiceUtility.getDeveloperStatus(response);
        String totalScore = NotionServiceUtility.getDeveloperTotalScore(response);
        return new Developer(name,id,githubUrl,
                githubImage,email,status, totalScore, Collections.emptyList());

    }

    @Cacheable(value = "developerScoreCard", key = "#id")
    public Developer getDeveloperByIdWithScore(UUID id) throws NotionException, NotionNotFoundException {
        List<Score> allScores = new ArrayList<>();
        String nextCursor = null;
        boolean hasMore = true;
        ObjectNode bodyNode = getDeveloperNode(id);
        while (hasMore) {
            if (nextCursor != null) {
                bodyNode.put("start_cursor", nextCursor);
            }
            JsonNode scoreResponse = notionApiService.fetchDatabase(SCORE_DATABASE_ID, bodyNode);

            if (scoreResponse.get("results").isEmpty()) {
                throw new NotionNotFoundException();
            }
            scoreResponse.get("results").elements().forEachRemaining(element -> {
                Score score = extractScore(element);
                if (score != null) {
                    allScores.add(score);
                }
            });
            nextCursor = scoreResponse.get("next_cursor").asText();
            hasMore = scoreResponse.get("has_more").asBoolean();
        }

        return Developer.addScore(getDeveloperById(id), allScores);
    }

    @Lazy
    @Cacheable(value = "allconsultants")
    public List<Consultant> getAllConsultants(boolean includeEmptyResponsible) throws NotionException {
        List<Consultant> allConsultants = new ArrayList<>();
        String nextCursor = null;
        boolean hasMore = true;
        List<String> ptPeople = getAllResponsiblePeople(false)
                .stream()
                .map(ResponsiblePerson::name)
                .toList();
        while (hasMore) {
            JsonNode response = notionApiService.fetchDatabase(DATABASE_ID, NotionServiceFilters.getFilterOnAssignment(nextCursor));
            response.get("results").elements().forEachRemaining(element -> {
                if (element.get("properties").get("Name") == null) {
                    return;
                }
                if (element.get("properties").get("Name").get("title").get(0) == null) {
                    return;
                }
                List<ResponsiblePerson> responsiblePersonList = getResponsiblePersonsFromResponse(element, ptPeople);
                if (responsiblePersonList.isEmpty() && !includeEmptyResponsible) {
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

    public Consultant getConsultantById(UUID id) throws NotionException {
        JsonNode response = notionApiService.fetchPage(id.toString());

        List<String> ptPeople = getAllResponsiblePeople(false)
                .stream()
                .map(ResponsiblePerson::name)
                .toList();

        List<ResponsiblePerson> responsiblePersonList = getResponsiblePersonsFromResponse(response, ptPeople);
        return new Consultant(
                response.get("properties").get("Name").get("title").get(0).get("plain_text").asText(),
                UUID.fromString(response.get("id").asText()),
                responsiblePersonList);
    }

    public List<ResponsiblePerson> getAllResponsiblePeople(boolean includeConsultants) throws NotionException {
        List<ResponsiblePerson> responsiblePersonList = new ArrayList<>();

        String jsonBody = NotionServiceFilters.FILTER_RESPONSIBLE_PEOPLE;

        JsonNode response = notionApiService.fetchDatabase(CORE_DATABASE_ID,jsonBody);
        response.get("results").elements().forEachRemaining(element -> {
            JsonNode person = element.get("properties").get("Person").get("people").get(0);
            if (person  == null) {
                return;
            }
//          If you add these lines then you can filter out inactive P&T
//            List<String> ptPeople = List.of(StringUtils.normalizeSwedishAlphabet(PEOPLE_AND_TALENT).split(","));
//            if (!ptPeople.contains(element.get("properties").get("Person").get("people").get(0).get("name").asText())) {
//                return;
//            }
            ResponsiblePerson responsiblePerson = new ResponsiblePerson(
                    person.get("name").asText(),
                    UUID.fromString(person.get("id").asText()),
                    person.get("person").get("email").asText(),
                    new ArrayList<>());

            responsiblePersonList.add(responsiblePerson);
        });

        if (includeConsultants) {
            List<Consultant> consultants = this.getAllConsultants(true);
            responsiblePersonList.forEach(responsiblePerson -> {
                List<Consultant> newConsultantsList = new ArrayList<>();
                consultants.forEach(consultant -> consultant.responsiblePersonList().forEach(consultantsResponsiblePerson -> {
                    if (consultantsResponsiblePerson.name() == null) return;
                    if (consultantsResponsiblePerson.id().equals(responsiblePerson.id())) {
                        newConsultantsList.add(consultant);
                    }
                }));
                newConsultantsList.forEach(consultant -> responsiblePerson.consultants().add(consultant));
            });
        }

        return responsiblePersonList;
    }

    public ResponsiblePerson getResponsiblePersonById(UUID id, boolean includeConsultants) throws NotionException {
        List<ResponsiblePerson> responsiblePersonList = getAllResponsiblePeople(includeConsultants);
        return responsiblePersonList.stream()
                .filter(responsiblePerson -> responsiblePerson.id().equals(id))
                .findFirst()
                .orElseThrow(()-> new NoSuchElementException("No responsible person found with id: " + id));
    }

    private ObjectNode getDeveloperNode(UUID id) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode relationNode = objectMapper.createObjectNode();
        relationNode.put("contains", String.valueOf(id));
        ObjectNode filterNode = objectMapper.createObjectNode();
        filterNode.put("property", "💽 Developer");
        filterNode.set("relation", relationNode);
        ObjectNode bodyNode = objectMapper.createObjectNode();
        bodyNode.set("filter", filterNode);
        return bodyNode;
    }

    private JsonNode createQueryRequestBody(String nextCursor) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode body = objectMapper.createObjectNode();
        if (nextCursor != null) {
            body.put("start_cursor", nextCursor);
        }
        System.out.println("body = " + body);
        return body;
    }

    private List<ResponsiblePerson> getResponsiblePersonsFromResponse(JsonNode response, List<String> ptPeople) {
        List<ResponsiblePerson> responsiblePersonList = new ArrayList<>();
        if (response.get("properties").get("Responsible").get("people").get(0) != null) {
            response.get("properties").get("Responsible").get("people").elements().forEachRemaining(element2 -> {

                if (element2.get("name") == null) return;

                String name = element2.get("name").asText();

                if (name != null) {
                    if (!ptPeople.contains(name)) {
                        return;
                    }
                }

                String email = null;
                if (element2.get("person") != null) {
                    if (element2.get("person").get("email") != null) {
                        email = element2.get("person").get("email").asText();
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

    private Score extractScore(JsonNode element) {
        if (element.get("properties").get("Score") == null) {
            return null;
        }

        List<String> categories = new ArrayList<>();
        if (element.get("properties").get("Categories") != null) {
            element.get("properties").get("Categories").get("multi_select").forEach(category ->
                    categories.add(category.get("name").asText()));
        }

        return new Score(
                element.get("properties").get("Name").get("title").get(0).get("plain_text").asText(),
                element.get("properties").get("Score").get("number").asInt(),
                categories,
                NotionServiceUtility.getScoreComment(element)
        );
    }

}