package com.saltpgp.notionproxy.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.saltpgp.notionproxy.exceptions.NotionException;
import com.saltpgp.notionproxy.exceptions.NotionNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

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

    public static class UriNotionFormat{
        public static final String PAGES = "/pages/%s";
        public static final String DATABASES = "/databases/%s/query";
    }

    public static class HeaderValue{
        public static final String CONTENT_TYPE = "application/json";
        public static final String BEARER = "Bearer ";
    }

    public static class RequestHeader{
        public static final String CONTENT_TYPE = "Content-Type";
        public static final String AUTHORIZATION = "Authorization";
        public static final String NOTION_VERSION = "Notion-Version";
    }

    public static class HttpClientCase {
        private static final String UNAUTHORIZED = "Unauthorized";
        private static final String BAD_REQUEST = "Bad Request";
        private static final String NOT_FOUND = "Not Found";
    }
    public static class ErrorNotionMessage{
        private static final String UNAUTHORIZED_TO_ACCESS = "Unauthorized to access Notion API. Check the API key.";
        private static final String BAD_REQUEST = "Bad request to the Notion API. Check the API request.";
        private static final String UNKNOWN_ERROR_WITH_HTTP_STATUS = "Unknown error occurred with HTTP status: ";
        private static final String CANT_ACCESS = "Can't access Notion API. Check if the Notion proxy can send requests.";
        private static final String UNKNOWN_ERROR_WHEN_SEND_REQUEST = "Unknown error occurred while trying to send request to Notion.";
        private static final String PAGE_ID_DIDNT_EXIST = "Page ID didn't exist in Notion: ";
        private static final String DATABASE_DIDNT_EXIST = "Database didn't exist in Notion";
    }
    public static class NotionType{
        private static final String PAGE = "page";
        private static final String DATABASE = "database";
    }

    @FunctionalInterface
    private interface RequestExecutor {
        JsonNode execute() throws Exception;
    }
}
