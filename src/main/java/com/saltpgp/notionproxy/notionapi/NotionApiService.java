package com.saltpgp.notionproxy.notionapi;

import com.fasterxml.jackson.databind.JsonNode;
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

    public JsonNode fetchDatabase(String database, Object node) throws NotionException {
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
