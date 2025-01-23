package com.saltpgp.notionproxy.notionapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saltpgp.notionproxy.exceptions.NotionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import com.saltpgp.notionproxy.notionapi.NotionApiConstants.*;

@Service
public class NotionApiService {

    public static final String NOTION_URL = "https://api.notion.com/v1";
    private final RestClient restClient;
    private final String API_KEY;
    private final String NOTION_VERSION;

    public NotionApiService(RestClient.Builder builder,
                            @Value("${NOTION_API_KEY}") String API_KEY,
                            @Value("${NOTION_VERSION}") String NOTION_VERSION) {
        this.restClient = builder.baseUrl(NOTION_URL).build();
        this.API_KEY = API_KEY;
        this.NOTION_VERSION = NOTION_VERSION;
    }

    public JsonNode fetchPage(String pageId) throws NotionException {
        try {
            // The provided JSON string
            String jsonString = """
                {
                    "object": "page",
                    "id": "11111111-1111-1111-1111-111111111111",
                    "properties": {
                        "Categories": {
                            "multi_select": [
                                {"name": "backend"},
                                {"name": "java"}
                            ]
                        },
                        "Score": {
                            "number": 100
                        },
                        "Name": {
                            "title": [
                                {"plain_text": "Three Small Methods"}
                            ]
                        }
                    }
                }
                """;

            // Create ObjectMapper instance
            ObjectMapper objectMapper = new ObjectMapper();

            // Convert the JSON string to a JsonNode
            JsonNode jsonNode = objectMapper.readTree(jsonString);

            // Return the JsonNode
            return jsonNode;
        } catch (Exception e) {
            String uri = String.format(UriNotionFormat.PAGES, pageId);
            return executeRequest(() -> restClient
                    .get()
                    .uri(uri)
                    .header(RequestHeader.CONTENT_TYPE, HeaderValue.CONTENT_TYPE)
                    .header(RequestHeader.AUTHORIZATION, HeaderValue.BEARER + API_KEY)
                    .header(RequestHeader.NOTION_VERSION, NOTION_VERSION)
                    .retrieve()
                    .body(JsonNode.class), pageId, NotionType.PAGE);
        }
    }

    public JsonNode fetchDatabase(String database, Object node) throws NotionException {
        try {
            // The provided JSON string
            String jsonString = """
                    {"object":"list","results":[{"object":"page","id":"11111111-1111-1111-1111-111111111111","properties":{"Categories":{"multi_select":[{"name":"backend"},{"name":"java"}]},"Score":{"number":100},"Name":{"title":[{"plain_text":"Three Small Methods"}]}}},{"object":"page","id":"22222222-2222-2222-2222-222222222222","properties":{"Categories":{"multi_select":[{"name":"frontend"},{"name":"javascript"}]},"Score":{"number":95},"Name":{"title":[{"plain_text":"UI Enhancement"}]}}},{"object":"page","id":"33333333-3333-3333-3333-333333333333","properties":{"Categories":{"multi_select":[{"name":"backend"},{"name":"python"}]},"Score":{"number":90},"Name":{"title":[{"plain_text":"Data Processing"}]}}},{"object":"page","id":"44444444-4444-4444-4444-444444444444","properties":{"Categories":{"multi_select":[{"name":"design"},{"name":"figma"}]},"Score":{"number":80},"Name":{"title":[{"plain_text":"UI/UX Design"}]}}},{"object":"page","id":"55555555-5555-5555-5555-555555555555","properties":{"Categories":{"multi_select":[{"name":"mobile"},{"name":"android"}]},"Score":{"number":85},"Name":{"title":[{"plain_text":"Mobile App"}]}}},{"object":"page","id":"66666666-6666-6666-6666-666666666666","properties":{"Categories":{"multi_select":[{"name":"backend"},{"name":"node.js"}]},"Score":{"number":92},"Name":{"title":[{"plain_text":"API Development"}]}}},{"object":"page","id":"77777777-7777-7777-7777-777777777777","properties":{"Categories":{"multi_select":[{"name":"frontend"},{"name":"vue.js"}]},"Score":{"number":88},"Name":{"title":[{"plain_text":"Web Application"}]}}},{"object":"page","id":"88888888-8888-8888-8888-888888888888","properties":{"Categories":{"multi_select":[{"name":"mobile"},{"name":"ios"}]},"Score":{"number":93},"Name":{"title":[{"plain_text":"iOS Development"}]}}},{"object":"page","id":"99999999-9999-9999-9999-999999999999","properties":{"Categories":{"multi_select":[{"name":"devops"},{"name":"docker"}]},"Score":{"number":97},"Name":{"title":[{"plain_text":"Containerization"}]}}},{"object":"page","id":"aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa","properties":{"Categories":{"multi_select":[{"name":"backend"},{"name":"go"}]},"Score":{"number":85},"Name":{"title":[{"plain_text":"GoLang Project"}]}}},{"object":"page","id":"bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb","properties":{"Categories":{"multi_select":[{"name":"frontend"},{"name":"react"}]},"Score":{"number":89},"Name":{"title":[{"plain_text":"React Components"}]}}},{"object":"page","id":"cccccccc-cccc-cccc-cccc-cccccccccccc","properties":{"Categories":{"multi_select":[{"name":"design"},{"name":"illustrator"}]},"Score":{"number":90},"Name":{"title":[{"plain_text":"Graphics Design"}]}}},{"object":"page","id":"dddddddd-dddd-dddd-dddd-dddddddddddd","properties":{"Categories":{"multi_select":[{"name":"backend"},{"name":"java"}]},"Score":{"number":96},"Name":{"title":[{"plain_text":"Spring Boot"}]}}},{"object":"page","id":"eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee","properties":{"Categories":{"multi_select":[{"name":"mobile"},{"name":"flutter"}]},"Score":{"number":94},"Name":{"title":[{"plain_text":"Cross-platform App"}]}}},{"object":"page","id":"ffffffff-ffff-ffff-ffff-ffffffffffff","properties":{"Categories":{"multi_select":[{"name":"backend"},{"name":"ruby"}]},"Score":{"number":91},"Name":{"title":[{"plain_text":"Ruby on Rails"}]}}},{"object":"page","id":"10101010-1010-1010-1010-101010101010","properties":{"Categories":{"multi_select":[{"name":"mobile"},{"name":"kotlin"}]},"Score":{"number":87},"Name":{"title":[{"plain_text":"Kotlin Mobile App"}]}}},{"object":"page","id":"20202020-2020-2020-2020-202020202020","properties":{"Categories":{"multi_select":[{"name":"backend"},{"name":"scala"}]},"Score":{"number":92},"Name":{"title":[{"plain_text":"Scala Services"}]}}},{"object":"page","id":"30303030-3030-3030-3030-303030303030","properties":{"Categories":{"multi_select":[{"name":"devops"},{"name":"terraform"}]},"Score":{"number":95},"Name":{"title":[{"plain_text":"Infrastructure Automation"}]}}},{"object":"page","id":"40404040-4040-4040-4040-404040404040","properties":{"Categories":{"multi_select":[{"name":"backend"},{"name":"c#"}]},"Score":{"number":89},"Name":{"title":[{"plain_text":"C# Web API"}]}}},{"object":"page","id":"50505050-5050-5050-5050-505050505050","properties":{"Categories":{"multi_select":[{"name":"frontend"},{"name":"angular"}]},"Score":{"number":90},"Name":{"title":[{"plain_text":"Angular Components"}]}}},{"object":"page","id":"60606060-6060-6060-6060-606060606060","properties":{"Categories":{"multi_select":[{"name":"mobile"},{"name":"swift"}]},"Score":{"number":96},"Name":{"title":[{"plain_text":"Swift iOS App"}]}}}],"next_cursor":null,"has_more":false}""";

            // Create ObjectMapper instance
            ObjectMapper objectMapper = new ObjectMapper();

            // Convert the JSON string to a JsonNode
            JsonNode jsonNode = objectMapper.readTree(jsonString);

            // Return the JsonNode
            return jsonNode;
        } catch (Exception e) {
            String uri = String.format(UriNotionFormat.DATABASES, database);
            return executeRequest(() -> restClient
                    .post()
                    .uri(uri)
                    .header(RequestHeader.CONTENT_TYPE, HeaderValue.CONTENT_TYPE)
                    .header(RequestHeader.AUTHORIZATION, HeaderValue.BEARER + API_KEY)
                    .header(RequestHeader.NOTION_VERSION, NOTION_VERSION)
                    .body(node)
                    .retrieve()
                    .body(JsonNode.class), database, NotionType.DATABASE);
        }
    }

    private JsonNode executeRequest(RequestExecutor executor, String id, String type) throws NotionException {
        try {
            return executor.execute();
        } catch (HttpClientErrorException e) {
            switch (e.getStatusText()) {
                case HttpClientCase.NOT_FOUND:
                    if (NotionType.PAGE.equals(type)) {
                        throw new NotionException(ErrorNotionMessage.PAGE_ID_DIDNT_EXIST + id);
                    }
                    if (NotionType.DATABASE.equals(type)) {
                        throw new NotionException(ErrorNotionMessage.DATABASE_DIDNT_EXIST);
                    }
                    break;
                case HttpClientCase.UNAUTHORIZED:
                    throw new NotionException(ErrorNotionMessage.UNAUTHORIZED_TO_ACCESS);
                case HttpClientCase.BAD_REQUEST:
                    throw new NotionException(ErrorNotionMessage.BAD_REQUEST);
                default:
                    throw new NotionException(ErrorNotionMessage.UNKNOWN_ERROR_WITH_HTTP_STATUS + e.getStatusText());
            }
        } catch (ResourceAccessException e) {
            throw new NotionException(ErrorNotionMessage.CANT_ACCESS);
        } catch (Exception e) {
            throw new NotionException(ErrorNotionMessage.UNKNOWN_ERROR_WHEN_SEND_REQUEST);
        }
        return null;
    }

    @FunctionalInterface
    private interface RequestExecutor {
        JsonNode execute() throws Exception;
    }
}
